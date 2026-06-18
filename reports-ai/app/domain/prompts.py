"""Prompt construction for NL -> SQL generation.

The brain never touches the database. The hard security guarantees live in
finance-api (AiSqlGuard + read-only role + READ ONLY tx). These prompts only
steer the model toward a single, safe, read-only SELECT against the given views.
"""
from __future__ import annotations

SYSTEM_RULES = """Sos un generador de SQL de solo lectura para PostgreSQL.
Reglas estrictas:
- Devolvé EXACTAMENTE UNA sentencia SELECT (o WITH ... SELECT). Nunca INSERT/UPDATE/DELETE/DDL.
- Usá únicamente las vistas listadas en el esquema provisto. No inventes tablas ni columnas.
- No uses pg_catalog, information_schema, ni funciones de sistema (pg_sleep, pg_read_file, dblink, etc.).
- No uses punto y coma múltiples ni varias sentencias.
- Respondé SOLO con un objeto JSON válido: {"sql": "...", "explanation": "..."}.
  - "sql": la consulta.
  - "explanation": una frase breve en español, sin datos personales.
"""

SCOPE_TENANT = """Alcance: TENANT.
- NO uses prefijos de schema (las vistas se resuelven por search_path).
- Consultá solo las vistas reporting_* del tenant.
"""

SCOPE_GLOBAL = """Alcance: GLOBAL (plataforma).
- Consultá solo las vistas del schema reporting (platform_overview, tenant_movement_ranking, etc.).
- Para comparar organizaciones, agrupá/ordená por la columna tenant_slug.
"""


def build_sql_generation_prompt(prompt: str, schema_description: str, scope: str) -> str:
    scope_block = SCOPE_GLOBAL if (scope or "").lower() == "global" else SCOPE_TENANT
    return (
        f"{SYSTEM_RULES}\n{scope_block}\n"
        f"Vistas disponibles:\n{schema_description}\n\n"
        f"Pedido del usuario:\n{prompt}\n\n"
        "Recordá: respondé solo el JSON {\"sql\": ..., \"explanation\": ...}."
    )
