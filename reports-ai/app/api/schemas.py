"""Request/response models for the reports-ai API."""
from __future__ import annotations

from typing import Any

from pydantic import BaseModel, Field


class GenerateSqlRequest(BaseModel):
    prompt: str = Field(min_length=1, max_length=2000)
    schema_description: str = Field(min_length=1)
    scope: str = "tenant"


class GenerateSqlResponse(BaseModel):
    sql: str
    explanation: str | None = None


class TranscribeAndGenerateResponse(BaseModel):
    transcript: str | None = None
    sql: str
    explanation: str | None = None


class ExplainResultRequest(BaseModel):
    prompt: str | None = None
    columns: list[str] = Field(default_factory=list)
    # Aggregates only — never raw rows with PII (counts, totals, top-N labels).
    aggregates: dict[str, Any] = Field(default_factory=dict)


class ExplainResultResponse(BaseModel):
    explanation: str
