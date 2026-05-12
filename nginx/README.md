# 🚀 Guía para construir y subir imágenes Docker (Flutter, Angular, API)

Esta guía muestra cómo construir y subir imágenes a Docker Hub usando variables genéricas:

* `TU_USUARIO`
* `VERSION`

---

# 🔐 1. Login en Docker Hub

```bash
docker login
```

---

# 🐳 2. Flutter Web

## 🔨 Build

```bash
docker build \
  -t TU_USUARIO/finance-flutter-web:VERSION \
  -f finance_mobile/Dockerfile.prod \
  ./finance_mobile
```

## 🚀 Push

```bash
docker push TU_USUARIO/finance-flutter-web:VERSION
```

---

# 🟢 3. Angular Web

## 🔨 Build

```bash
docker build \
  -t TU_USUARIO/finance-web:VERSION \
  -f finance-web/Dockerfile.prod \
  ./finance-web
```

## 🚀 Push

```bash
docker push TU_USUARIO/finance-web:VERSION
```

---

# 🔵 4. API (Spring Boot)

## 🔨 Build

```bash
docker build \
  -t TU_USUARIO/finance-api:VERSION \
  -f finance-api/Dockerfile.prod \
  ./finance-api
```

## 🚀 Push

```bash
docker push TU_USUARIO/finance-api:VERSION
```

---

# 🧪 5. Verificar imágenes locales

```bash
docker images
```

Deberías ver:

```
TU_USUARIO/finance-flutter-web   VERSION
TU_USUARIO/finance-web           VERSION
TU_USUARIO/finance-api           VERSION
```

---

# 🐳 6. Uso en docker-compose (producción)

```yaml
services:
  serv-finance-api:
    image: TU_USUARIO/finance-api:VERSION

  serv-finance-web:
    image: TU_USUARIO/finance-web:VERSION

  serv-finance-flutter-web:
    image: TU_USUARIO/finance-flutter-web:VERSION
```

---

# 🚀 7. Despliegue en servidor

```bash
docker compose pull
docker compose up -d
```

---

# 🧠 Buenas prácticas

* Usa versiones fijas (`1.0.0`, `1.0.1`, etc.)
* No sobrescribas versiones ya publicadas
* Usa `latest` solo en desarrollo si lo necesitas

---

# 🧾 Resumen rápido

```bash
docker build -t TU_USUARIO/servicio:VERSION -f ruta/Dockerfile.prod ./ruta
docker push TU_USUARIO/servicio:VERSION
```

---

# 🎯 Ejemplo real

```bash
docker build -t sebastiandevjs/finance-api:1.0.0 -f finance-api/Dockerfile.prod ./finance-api
docker push sebastiandevjs/finance-api:1.0.0
```

---
