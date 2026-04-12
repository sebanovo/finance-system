import { Routes } from "@angular/router";

export const TENANTS_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/tenant-list-page/tenant-list-page").then(
        m => m.TenantListPage
      ),
    // Block route until it is ready
    // resolve: {
    //   tenants: tenantListResolver,
    // },
  },
];
