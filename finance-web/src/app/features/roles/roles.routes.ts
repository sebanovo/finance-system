import { Routes } from "@angular/router";

export const ROLES_ROUTES: Routes = [
  {
    path: "", //default route
    redirectTo: "list",
    pathMatch: "full",
  },
  {
    path: "list",
    loadComponent: () =>
      import("./pages/role-list-page/role-list-page").then(m => m.RoleListPage),
  },

  {
    path: "create",
    loadComponent: () =>
      import("./pages/role-create-page/role-create-page").then(
        m => m.RoleCreatePage
      ),
  },
  {
    path: ":id/edit",
    loadComponent: () =>
      import("./pages/role-edit-page/role-edit-page").then(m => m.RoleEditPage),
  },
];
