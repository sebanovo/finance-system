export type AppPageTitleItem = {
  route: string;
  title: string;
  subtitle: string;
};

export const APP_PAGE_TITLES: AppPageTitleItem[] = [
  {
    route: "/app/dashboard",
    title: "Dashboard",
    subtitle: "Vista general",
  },
  {
    route: "/app/users",
    title: "Usuarios",
    subtitle: "Gestión de usuarios",
  },
  {
    route: "/app/products",
    title: "Productos",
    subtitle: "Gestión de productos",
  },
  {
    route: "/app/reports",
    title: "Reportes",
    subtitle: "Indicadores y métricas",
  },
  {
    route: "/app/settings",
    title: "Configuración",
    subtitle: "Preferencias del sistema",
  },
];
