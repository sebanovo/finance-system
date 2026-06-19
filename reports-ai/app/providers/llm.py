"""NL -> SQL providers: mock | gemini | openrouter.

The returned SQL is never trusted by this service; finance-api re-validates it
with AiSqlGuard before executing under a read-only role.
"""
from __future__ import annotations

import json
import re

import httpx

from app.config import settings
from app.domain.prompts import build_sql_generation_prompt


class LlmError(RuntimeError):
    pass


def _raise_clean(provider: str, response: httpx.Response) -> None:
    """Raise a sanitized LlmError (no URL/api-key) from an upstream HTTP error."""
    status = response.status_code
    if status < 400:
        return
    if status == 429:
        raise LlmError(f"{provider}: límite de solicitudes alcanzado (429). "
                       "Esperá un momento o usá otro modelo/clave.") from None
    if status in (401, 403):
        raise LlmError(f"{provider}: credencial inválida o sin permisos ({status}).") from None
    raise LlmError(f"{provider} devolvió HTTP {status}.") from None


def _canned_sql(scope: str) -> str:
    if (scope or "").lower() == "global":
        return ("SELECT tenant_slug, transaction_count, total_amount "
                "FROM tenant_movement_ranking ORDER BY total_amount DESC")
    return ("SELECT transaction_id, created_at, transaction_type, status, amount, currency "
            "FROM reporting_tenant_movements ORDER BY created_at DESC")


def _extract_json(text: str) -> dict:
    """Pull the first JSON object out of a model response (handles code fences)."""
    cleaned = text.strip()
    cleaned = re.sub(r"^```(?:json)?", "", cleaned).strip()
    cleaned = re.sub(r"```$", "", cleaned).strip()
    start = cleaned.find("{")
    end = cleaned.rfind("}")
    if start == -1 or end == -1 or end <= start:
        raise LlmError("La respuesta del modelo no contiene JSON.")
    return json.loads(cleaned[start:end + 1])


def _normalize(payload: dict, scope: str) -> dict:
    sql = (payload.get("sql") or "").strip()
    if not sql:
        raise LlmError("El modelo no devolvió SQL.")
    return {"sql": sql, "explanation": payload.get("explanation")}


async def generate_sql(prompt: str, schema_description: str, scope: str) -> dict:
    provider = settings.ai_provider.lower()
    if provider == "gemini":
        return await _generate_gemini(prompt, schema_description, scope)
    if provider == "openrouter":
        return await _generate_openrouter(prompt, schema_description, scope)
    return {"sql": _canned_sql(scope), "explanation": "Respuesta simulada (modo mock)."}


async def _generate_gemini(prompt: str, schema_description: str, scope: str) -> dict:
    if not settings.gemini_api_key:
        raise LlmError("GEMINI_API_KEY no configurada.")
    full_prompt = build_sql_generation_prompt(prompt, schema_description, scope)
    url = (f"https://generativelanguage.googleapis.com/v1beta/models/"
           f"{settings.gemini_model}:generateContent?key={settings.gemini_api_key}")
    body = {
        "contents": [{"parts": [{"text": full_prompt}]}],
        "generationConfig": {"response_mime_type": "application/json", "temperature": 0.1},
    }
    try:
        async with httpx.AsyncClient(timeout=settings.llm_timeout_seconds) as client:
            response = await client.post(url, json=body)
    except httpx.RequestError:
        raise LlmError("No se pudo contactar a Gemini.") from None
    _raise_clean("Gemini", response)
    data = response.json()
    try:
        text = data["candidates"][0]["content"]["parts"][0]["text"]
    except (KeyError, IndexError) as exc:
        raise LlmError("Respuesta inesperada de Gemini.") from exc
    return _normalize(_extract_json(text), scope)


async def _generate_openrouter(prompt: str, schema_description: str, scope: str) -> dict:
    if not settings.openrouter_api_key:
        raise LlmError("OPENROUTER_API_KEY no configurada.")
    full_prompt = build_sql_generation_prompt(prompt, schema_description, scope)
    headers = {"Authorization": f"Bearer {settings.openrouter_api_key}"}
    body = {
        "model": settings.openrouter_model,
        "messages": [{"role": "user", "content": full_prompt}],
        "response_format": {"type": "json_object"},
        "temperature": 0.1,
    }
    try:
        async with httpx.AsyncClient(timeout=settings.llm_timeout_seconds) as client:
            response = await client.post("https://openrouter.ai/api/v1/chat/completions",
                                         headers=headers, json=body)
    except httpx.RequestError:
        raise LlmError("No se pudo contactar a OpenRouter.") from None
    _raise_clean("OpenRouter", response)
    data = response.json()
    try:
        text = data["choices"][0]["message"]["content"]
    except (KeyError, IndexError) as exc:
        raise LlmError("Respuesta inesperada de OpenRouter.") from exc
    return _normalize(_extract_json(text), scope)
