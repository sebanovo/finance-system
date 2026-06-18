"""API routes for the reports-ai brain. No database access."""
from __future__ import annotations

from fastapi import APIRouter, Depends, File, Form, Header, HTTPException, Request, UploadFile

from app.api.schemas import (
    ExplainResultRequest,
    ExplainResultResponse,
    GenerateSqlRequest,
    GenerateSqlResponse,
    TranscribeAndGenerateResponse,
)
from app.config import settings
from app.providers import llm, stt
from app.ratelimit import RateLimiter

router = APIRouter()
_rate_limiter = RateLimiter(max_requests=30, window_seconds=60)


def require_internal_token(x_internal_token: str | None = Header(default=None)) -> None:
    """Service-to-service auth. Enforced only when a token is configured."""
    if settings.internal_token and x_internal_token != settings.internal_token:
        raise HTTPException(status_code=401, detail="Token interno inválido.")


def _throttle(request: Request) -> None:
    client = request.client.host if request.client else "unknown"
    if not _rate_limiter.allow(client):
        raise HTTPException(status_code=429, detail="Demasiadas solicitudes, intentá más tarde.")


@router.post("/generate-sql", response_model=GenerateSqlResponse,
             dependencies=[Depends(require_internal_token)])
async def generate_sql(request: Request, body: GenerateSqlRequest) -> GenerateSqlResponse:
    _throttle(request)
    try:
        result = await llm.generate_sql(body.prompt, body.schema_description, body.scope)
    except llm.LlmError as exc:
        raise HTTPException(status_code=502, detail=str(exc))
    except Exception as exc:  # noqa: BLE001 - upstream/provider failure
        raise HTTPException(status_code=502, detail=f"Fallo del proveedor LLM: {exc}")
    return GenerateSqlResponse(**result)


@router.post("/transcribe-and-generate", response_model=TranscribeAndGenerateResponse,
             dependencies=[Depends(require_internal_token)])
async def transcribe_and_generate(
    request: Request,
    audio: UploadFile = File(...),
    schema_description: str = Form(...),
    scope: str = Form("tenant"),
) -> TranscribeAndGenerateResponse:
    _throttle(request)

    content = await audio.read()
    if len(content) == 0:
        raise HTTPException(status_code=400, detail="El audio está vacío.")
    if len(content) > settings.audio_max_bytes:
        raise HTTPException(status_code=413, detail="El audio excede el tamaño máximo permitido.")
    if audio.content_type and audio.content_type not in settings.allowed_mimes():
        raise HTTPException(status_code=415, detail=f"Formato de audio no soportado: {audio.content_type}")

    try:
        transcript = await stt.transcribe(content, audio.filename or "audio", audio.content_type or "")
    except stt.SttError as exc:
        raise HTTPException(status_code=502, detail=str(exc))
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=502, detail=f"Fallo del proveedor STT: {exc}")

    try:
        result = await llm.generate_sql(transcript, schema_description, scope)
    except llm.LlmError as exc:
        raise HTTPException(status_code=502, detail=str(exc))
    except Exception as exc:  # noqa: BLE001
        raise HTTPException(status_code=502, detail=f"Fallo del proveedor LLM: {exc}")

    return TranscribeAndGenerateResponse(transcript=transcript, sql=result["sql"],
                                         explanation=result.get("explanation"))


@router.post("/explain-result", response_model=ExplainResultResponse,
             dependencies=[Depends(require_internal_token)])
async def explain_result(body: ExplainResultRequest) -> ExplainResultResponse:
    """Best-effort, deterministic summary from columns + aggregates only.

    Never receives raw rows with PII, so it never leaks personal data.
    """
    parts: list[str] = []
    row_count = body.aggregates.get("rowCount")
    if row_count is not None:
        parts.append(f"El reporte devolvió {row_count} fila(s).")
    if body.columns:
        parts.append("Columnas: " + ", ".join(body.columns[:8]) + ".")
    totals = body.aggregates.get("totals")
    if isinstance(totals, dict) and totals:
        joined = ", ".join(f"{k}={v}" for k, v in list(totals.items())[:5])
        parts.append(f"Totales: {joined}.")
    grouped = body.aggregates.get("groupedSummary")
    if isinstance(grouped, list) and grouped:
        parts.append(f"Se observan {len(grouped)} grupos principales.")
    if not parts:
        parts.append("Resultado generado correctamente.")
    return ExplainResultResponse(explanation=" ".join(parts))
