import { AuthMeData } from "../../app/features/auth/models/auth-request.type";

export const usersMock: AuthMeData[] = [
  {
    id: "user1",
    email: "admin@tenant.com",
    firstName: "John",
    lastName: "Doe",
    active: true,
    status: "active",
    tenantSlug: "tenant123",
    roles: ["admin"],
  },
  {
    id: "user2",
    email: "user@tenant.com",
    firstName: "Jane",
    lastName: "Doe",
    active: true,
    status: "inactive",
    tenantSlug: "tenant123",
    roles: ["user"],
  },
  {
    id: "user3",
    email: "manager@tenant.com",
    firstName: "Tom",
    lastName: "Smith",
    active: true,
    status: "active",
    tenantSlug: "tenant456",
    roles: ["manager"],
  },
];
