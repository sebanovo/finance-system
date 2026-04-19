import { AuthMeData, AuthMeDto } from "../models/auth-request.type";

export function toAuthMe(dto: AuthMeDto): AuthMeData {
  return {
    id: dto.id,
    email: dto.email,
    firstName: dto.firstName,
    lastName: dto.lastName,
    active: dto.active,

    roles: dto.roles ?? [],

    status: dto.status,
    tenantSlug: dto.tenantSlug,

    createdAt: dto.createdAt
      ? new Date(dto.createdAt).toISOString()
      : undefined,

    updatedAt: dto.updatedAt
      ? new Date(dto.updatedAt).toISOString()
      : undefined,
  };
}
