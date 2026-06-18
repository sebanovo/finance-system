# reports-ai

The NL→SQL + STT "brain" for the finance-system reporting module. **It never
touches the database** — finance-api validates every generated SQL with
`AiSqlGuard` and runs it under a read-only role.

## Endpoints
- `POST /generate-sql` — `{prompt, schema_description, scope}` → `{sql, explanation}`
- `POST /transcribe-and-generate` — multipart `audio` + `schema_description` + `scope` → `{transcript, sql, explanation}`
- `POST /explain-result` — `{prompt, columns, aggregates}` → `{explanation}` (best-effort, aggregates only, no PII)
- `GET /health`

All POST endpoints require the `X-Internal-Token` header when `INTERNAL_TOKEN` is set.

## Providers
- LLM (`AI_PROVIDER`): `mock` (default) · `gemini` · `openrouter`
- STT (`STT_PROVIDER`): `mock` (default) · `groq` (whisper-large-v3)

## Run locally
```bash
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --port 8000
```

## Wiring with finance-api
Set `REPORTS_AI_MODE=http` and `REPORTS_AI_BASE_URL=http://reports-ai:8000` in the
root `.env`, and make `REPORTS_AI_INTERNAL_TOKEN` match this service's `INTERNAL_TOKEN`.
With `REPORTS_AI_MODE=mock`, finance-api uses its own local mock and never calls this service.
