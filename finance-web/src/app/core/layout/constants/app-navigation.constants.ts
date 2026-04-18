import { AppNavigationItem } from "../models/navigation.type";

export const APP_NAVIGATION: AppNavigationItem[] = [
  {
    type: "section",
    label: "Operación",
  },
  {
    label: "Dashboard",
    route: "/app/dashboard",
    exact: true,
    icon: {
      type: "remix",
      name: "ri-dashboard-line",
    },
  },
  {
    label: "Tenants",
    route: "/app/tenants",
    icon: {
      type: "remix",
      name: "ri-building-line",
    },
  },
  {
    label: "Suscripciones",
    route: "/app/subscriptions",
    icon: {
      type: "remix",
      name: "ri-vip-crown-line",
    },
  },
  {
    label: "Usuarios",
    route: "/app/users",
    icon: {
      type: "lucide",
      name: "users",
    },
  },

  // {
  //   label: "Reportes",
  //   route: "/app/reports",
  //   icon: {
  //     type: "remix",
  //     name: "ri-bar-chart-box-line",
  //   },
  // },

  {
    type: "section",
    label: "Administración",
  },
  {
    label: "Roles",
    route: "/app/roles",
    icon: {
      type: "lucide",
      name: "shield",
    },
  },
  {
    label: "Permisos",
    route: "/app/permissions",
    icon: {
      type: "remix",
      name: "ri-key-line",
    },
  },
  {
    label: "Planes",
    route: "/app/plans",
    icon: {
      type: "remix",
      name: "ri-price-tag-3-line",
    },
  },
  // {
  //   label: "Auditoría",
  //   route: "/app/audit",
  //   icon: {
  //     type: "remix",
  //     name: "ri-file-list-3-line",
  //   },
  // },
  {
    label: "Settings",
    route: "/app/settings",
    icon: {
      type: "lucide",
      name: "settings",
    },
  },
];
