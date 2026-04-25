export type AuthItem = {
  id: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  active: boolean;
  status: string;
  tenantSlug: string;
  roles: string[];
  timeStamp: string;
};

export const usersMock: AuthItem[] = [
  {
    id: "user0",
    email: "admin@tenant.com",
    password: "admin123", // Contraseña simulada
    firstName: "John",
    lastName: "Doe",
    active: true,
    status: "active",
    tenantSlug: "tenant123",
    roles: ["admin", "super-admin"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
  {
    id: "user1",
    email: "user@tenant.com",
    password: "user123", // Contraseña simulada
    firstName: "Jane",
    lastName: "Smith",
    active: true,
    status: "active",
    tenantSlug: "tenant124",
    roles: ["user", "guest"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
  {
    id: "user2",
    email: "manager@tenant.com",
    password: "manager123", // Contraseña simulada
    firstName: "Tom",
    lastName: "Jones",
    active: true,
    status: "active",
    tenantSlug: "tenant125",
    roles: ["manager", "supervisor"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
  {
    id: "user3",
    email: "support@tenant.com",
    password: "support123", // Contraseña simulada
    firstName: "Emily",
    lastName: "Davis",
    active: true,
    status: "active",
    tenantSlug: "tenant126",
    roles: ["support", "admin"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
  {
    id: "user4",
    email: "customer@tenant.com",
    password: "customer123", // Contraseña simulada
    firstName: "Michael",
    lastName: "Taylor",
    active: true,
    status: "active",
    tenantSlug: "tenant127",
    roles: ["customer", "premium-customer"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
  {
    id: "tu-tigre",
    email: "thunder@gmail.com",
    password: "thunder12345", // Contraseña simulada
    firstName: "Thor",
    lastName: "Odinson",
    active: true,
    status: "active",
    tenantSlug: "thunder",
    roles: ["tenant-owner", "god"], // Rol adicional agregado
    timeStamp: "2026-04-11T23:19:01.125Z", // Estático
  },
];
