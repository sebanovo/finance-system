import { Routes } from "@angular/router";

export const PERMISSIONS_ROUTES: Routes = [
  {
    path: "", //default route
    redirectTo: "list",
    pathMatch: "full",
  },
  {
    path: "",
    loadComponent: () =>
      import("./pages/permission-list-page/permission-list-page").then(
        m => m.PermissionListPage
      ),
  },
];
