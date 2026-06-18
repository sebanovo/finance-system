"""Contract tests for reports-ai using the mock providers.

Run with: pip install -r requirements.txt pytest && pytest
"""
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_generate_sql_tenant_mock():
    response = client.post("/generate-sql", json={
        "prompt": "movimientos recientes",
        "schema_description": "reporting_tenant_movements",
        "scope": "tenant",
    })
    assert response.status_code == 200
    body = response.json()
    assert "reporting_tenant_movements" in body["sql"]
    assert body["explanation"]


def test_generate_sql_global_mock():
    response = client.post("/generate-sql", json={
        "prompt": "ranking de tenants",
        "schema_description": "tenant_movement_ranking",
        "scope": "global",
    })
    assert response.status_code == 200
    assert "tenant_movement_ranking" in response.json()["sql"]


def test_transcribe_and_generate_mock():
    files = {"audio": ("clip.webm", b"fake-audio-bytes", "audio/webm")}
    data = {"schema_description": "reporting_tenant_movements", "scope": "tenant"}
    response = client.post("/transcribe-and-generate", files=files, data=data)
    assert response.status_code == 200
    body = response.json()
    assert body["transcript"]
    assert body["sql"]


def test_explain_result_no_pii():
    response = client.post("/explain-result", json={
        "columns": ["tenant_slug", "total_amount"],
        "aggregates": {"rowCount": 12, "totals": {"total_amount": 9999}},
    })
    assert response.status_code == 200
    assert "12" in response.json()["explanation"]
