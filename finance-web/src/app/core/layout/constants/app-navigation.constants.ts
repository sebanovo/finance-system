export type AppNavigationItem = {
  label: string;
  route: string;
};

export const APP_NAVIGATION: AppNavigationItem[] = [
  {
    label: "Dashboard",
    route: "/app/dashboard",
  },
  {
    label: "Usuarios",
    route: "/app/users",
  },
  {
    label: "Productos",
    route: "/app/products",
  },
  {
    label: "Reportes",
    route: "/app/reports",
  },
  {
    label: "Configuración",
    route: "/app/settings",
  },
];
