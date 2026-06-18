"""reports-ai — the NL->SQL + STT brain. No database access."""
from __future__ import annotations

from fastapi import FastAPI

from app.api.routes import router
from app.config import settings

app = FastAPI(title="reports-ai", version="1.0.0", description="NL->SQL + STT brain for finance-system reports")

app.include_router(router)


@app.get("/health")
def health() -> dict:
    return {
        "status": "ok",
        "ai_provider": settings.ai_provider,
        "stt_provider": settings.stt_provider,
    }
