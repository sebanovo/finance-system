# 🚀 Guía para construir y subir imágenes Docker (Flutter, Angular, API)

Esta guía muestra cómo construir y subir imágenes a Docker Hub usando variables genéricas:

* `TU_USUARIO`
* `1.0.0`

# 1. Build de todas las imágenes
```bash
docker build -t sebastiandevjs/finance-api:1.0.0         ./finance-api    -f ./finance-api/Dockerfile.prod
docker build -t sebastiandevjs/reports-ai:1.0.0          ./reports-ai    -f ./reports-ai/Dockerfile.prod
docker build -t sebastiandevjs/finance-web:1.0.0         ./finance-web    -f ./finance-web/Dockerfile.prod

# docker build -t sebastiandevjs/finance-flutter-web:1.0.0 ./finance_mobile -f ./finance_mobile/Dockerfile.prod
```

# 2. Login en Docker Hub
```bash
docker login
```

# 3. Push de todas las imágenes
```bash
docker push sebastiandevjs/finance-api:1.0.0
docker push sebastiandevjs/reports-ai:1.0.0
docker push sebastiandevjs/finance-web:1.0.0

# docker push sebastiandevjs/finance-flutter-web:1.0.0
```