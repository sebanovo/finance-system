import { Routes } from "@angular/router";

export const USERS_ROUTES: Routes = [
  {
    path: "", //default route
    redirectTo: "list",
    pathMatch: "full",
  },
  {
    path: "list",
    loadComponent: () =>
      import("./pages/user-list-page/user-list-page").then(m => m.UserListPage),
  },
  {
    path: "create",
    loadComponent: () =>
      import("./pages/user-create-page/user-create-page").then(
        m => m.UserCreatePage
      ),
  },
];
