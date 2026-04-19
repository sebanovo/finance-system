import { Routes } from "@angular/router";

export const PLANS_ROUTES: Routes = [
  {
    path: "", //default route
    redirectTo: "list",
    pathMatch: "full",
  },
  {
    path: "",
    loadComponent: () =>
      import("./pages/plan-list-page/plan-list-page").then(m => m.PlanListPage),
  },

  {
    path: "list",
    loadComponent: () =>
      import("./pages/plan-list-page/plan-list-page").then(m => m.PlanListPage),
  },
  {
    path: "create",
    loadComponent: () =>
      import("./pages/plan-create-page/plan-create-page").then(
        m => m.PlanCreatePage
      ),
  },

  // {
  //   path: ":id/edit",
  //   loadComponent: () =>
  //     import("./pages/plan-edit-page/plan-edit-page").then(m => m.PlanEditPage),
  // },
];
