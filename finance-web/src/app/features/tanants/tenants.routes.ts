import { Routes } from "@angular/router";

export const TENANTS_ROUTES: Routes = [
  {
    path: "", //default route
    redirectTo: "list",
    pathMatch: "full",
  },
  {
    path: "list",
    loadComponent: () =>
      import("./pages/tenant-list-page/tenant-list-page").then(
        m => m.TenantListPage
      ),
    // Block route until it is ready
    // resolve: {
    //   tenants: tenantListResolver,
    // },
  },
  {
    path: "create", //default route
    loadComponent: () =>
      import("./pages/tenant-create-page/tenant-create-page").then(
        m => m.TenantCreatePage
      ),
  },

  {
    path: "detail", //default route
    loadComponent: () =>
      import("./pages/tenant-detail-page/tenant-detail-page").then(
        m => m.TenantDetailPage
      ),
  },
];
