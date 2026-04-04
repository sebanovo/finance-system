# Sistema financiero

### Servicios (Docker)

| Servicio | Tecnología  | Puerto | Descripción   |
| -------- | ----------- | ------ | ------------- |
| serv-web | Angular     | 4200   | Interfaz web  |
| serv-api | Spring Boot | 8080   | API REST      |
| ser-db   | PostgreSQL  | 5432   | Base de datos |

---

### 🧯 Comandos Docker

```bash
# Iniciar servicios
docker compose up --build

# Iniciar en background
docker compose up --build -d

# Ver logs
docker compose logs -f

# Detener servicios
docker compose down

# Resetear base de datos
docker compose down -v
```

## 🧯 Ejecutar DevContainer en VSCode

### ⚠️ instalar devcontainer en VSCODE

1. **En VSCode**  
   Abrir `finance-api` o `finance-web`.

2. **En otra ventana Ejecutar la base de datos (solo para finance-api)**

   ```bash
   docker compose up --build serv-db

   ```

3. **Abrir el Command Palette**

- Presiona `Ctrl + Shift + P`.
- Busca y selecciona la opción `Dev Containers: Reopen in Container.`

5. **Ejecutar proyectos**

- En `finance-api`

  ```bash
  ./mvnw spring-boot:run
  ```

- En `finance-web`
  ```bash
  npm start
  ```
- Listo !!!

last modified: 2/04/2026
