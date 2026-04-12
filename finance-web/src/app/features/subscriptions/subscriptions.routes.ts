import { Routes } from "@angular/router";

export const SUBSCRIPTIONS_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/subscription-list-page/subscription-list-page").then(
        m => m.SubscriptionListPage
      ),
  },
];
