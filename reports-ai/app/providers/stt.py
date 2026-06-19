"""Speech-to-text providers: mock | groq."""
from __future__ import annotations

import httpx

from app.config import settings


class SttError(RuntimeError):
    pass


async def transcribe(audio: bytes, filename: str, mime_type: str) -> str:
    provider = settings.stt_provider.lower()
    if provider == "groq":
        return await _transcribe_groq(audio, filename, mime_type)
    return "Mostrar los movimientos recientes (transcripción simulada)."


async def _transcribe_groq(audio: bytes, filename: str, mime_type: str) -> str:
    if not settings.groq_api_key:
        raise SttError("GROQ_API_KEY no configurada.")
    headers = {"Authorization": f"Bearer {settings.groq_api_key}"}
    files = {"file": (filename or "audio", audio, mime_type or "application/octet-stream")}
    payload = {"model": settings.groq_model, "language": "es", "response_format": "json"}
    try:
        async with httpx.AsyncClient(timeout=settings.stt_timeout_seconds) as client:
            response = await client.post(
                "https://api.groq.com/openai/v1/audio/transcriptions",
                headers=headers, data=payload, files=files,
            )
    except httpx.RequestError:
        raise SttError("No se pudo contactar a Groq.") from None
    if response.status_code == 429:
        raise SttError("Groq: límite de solicitudes alcanzado (429). Esperá un momento.") from None
    if response.status_code in (401, 403):
        raise SttError(f"Groq: credencial inválida o sin permisos ({response.status_code}).") from None
    if response.status_code >= 400:
        raise SttError(f"Groq devolvió HTTP {response.status_code}.") from None
    data = response.json()
    text = (data.get("text") or "").strip()
    if not text:
        raise SttError("La transcripción quedó vacía.")
    return text
