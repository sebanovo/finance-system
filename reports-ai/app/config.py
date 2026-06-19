"""Service configuration loaded from environment variables."""
from __future__ import annotations

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    # Service-to-service auth (must match finance-api REPORTS_AI_INTERNAL_TOKEN).
    internal_token: str = ""

    # NL -> SQL provider: mock | gemini | openrouter
    ai_provider: str = "mock"
    gemini_api_key: str = ""
    gemini_model: str = "gemini-2.0-flash"
    openrouter_api_key: str = ""
    openrouter_model: str = "google/gemini-2.0-flash-001"
    llm_timeout_seconds: float = 20.0

    # Speech-to-text provider: mock | groq
    stt_provider: str = "mock"
    groq_api_key: str = ""
    groq_model: str = "whisper-large-v3"
    stt_timeout_seconds: float = 30.0

    # Audio limits
    audio_max_bytes: int = 10 * 1024 * 1024  # 10 MB
    audio_allowed_mimes: str = "audio/webm,audio/wav,audio/x-wav,audio/mpeg,audio/mp3,audio/mp4,audio/m4a,audio/x-m4a,application/octet-stream"

    def allowed_mimes(self) -> set[str]:
        return {m.strip() for m in self.audio_allowed_mimes.split(",") if m.strip()}


settings = Settings()
