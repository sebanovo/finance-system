// DTO para la creación de una nueva empresa (Enviado al backend)
export interface CreateTenantRequest {
  name: string;
  slug: string;
  planCode?: string; // Opcional, dependiendo de si asignas el plan al crear
}

// DTO de la respuesta del backend al listar o crear
export interface PlatformTenantResponse {
  id: string;
  name: string;
  slug: string;
  schemaName: string;
  status: string; // Ej: 'ACTIVE', 'PENDING'
  planId: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}