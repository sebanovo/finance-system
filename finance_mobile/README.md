# finance_mobile

A new Flutter project.

Importante ejecutar este comando para el .env: 
```bash
cp finance_mobile/.env.sample finance_mobile/.env
```

```bash
# Ejemplo sencillo de la arquitectura con permission
└── 📁core
    └── 📁di (service locator, centraliza las dependencias)
        ├── injection_container.dart 
    └── 📁error (manejo de errores personalizados)
        ├── failures.dart
    └── 📁network (configuracion del cliente usar dio y http por ahora)
        ├── api_client.dart
    └── 📁routes (configuración de las rutas de la aplicación)
        └── app_routes.dart

└── 📁domain
    └── 📁entities (entidades)
        ├── permission.dart
    └── 📁repositories (repositorios)
        ├── permission_repository.dart
    └── 📁usecases (casos de uso)
        └── get_permissions_usecase.dart

└── 📁infrastructure
    └── 📁datasources
        ├── permission_remote_datasource.dart
    └── 📁models
        ├── permission.dart
    └── 📁repositories
        ├── permission_repository_impl.dart

└── 📁presentation
    └── 📁pages
        ├── permissions_pages.dart
    └── 📁viewmodels
        ├── permissions_viewmodel.dart
    └── 📁widgets
        └── change_password_dialog.dart
```


```bash
Clean Code Flow
domain:
(entities)Permission 
|  |
|  |->(repositories)permission_repository "interface"
|  |
|--|->(usecases)get_permissions_usecase


infrastructure:
(models)permission
  |
  |->(datasources)permission_remote_datasource "interface" + "class" -> (class implements inteface)
  |
  |->(repositories)permission_repository_impl (class implements interface off domain/repositories)


presentation:
(viewmodels)permissions_viewmodel usa domain/entities y domain/usecases
  |
  |->(pages)permissions_page usa core/di

```