import { Routes } from "@angular/router";

export const ACCESS_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/access-list-page/access-list-page").then(
        m => m.AccessListPage
      ),
  },
];
