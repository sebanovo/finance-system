import { Routes } from "@angular/router";
import { adminSubscriptionsMatchGuard } from "../../core/session/guards/admin-subscription.guard";
import { subscriptionsFallbackGuard } from "../../core/session/guards/subscription-fallback.guard";
import { userSubscriptionsMatchGuard } from "../../core/session/guards/user-subscription.guard";

export const SUBSCRIPTIONS_ROUTES: Routes = [
  {
    path: "",
    canMatch: [adminSubscriptionsMatchGuard],
    loadComponent: () =>
      import("./pages/subscription-list-page/subscription-list-page").then(
        m => m.SubscriptionListPage
      ),
  },
  {
    path: "",
    canMatch: [userSubscriptionsMatchGuard],
    loadComponent: () =>
      import("./pages/current-subscription-page/current-subscription-page").then(
        m => m.CurrentSubscriptionPage
      ),
  },
  {
    path: "",
    canActivate: [subscriptionsFallbackGuard],
    loadComponent: () =>
      import("./pages/router-emtpy-page/router-emtpy-page").then(
        m => m.RouterEmtpyPage
      ),
  },

  // {
  //   path: "",
  //   canActivate: [subscriptionGuard],
  //   children: [
  //     {
  //       path: "list",
  //       loadComponent: () =>
  //         import("./pages/subscription-list-page/subscription-list-page").then(
  //           m => m.SubscriptionListPage
  //         ),
  //     },
  //     {
  //       path: "current",
  //       loadComponent: () =>
  //         import("./pages/current-subscription-page/current-subscription-page").then(
  //           m => m.CurrentSubscriptionPage
  //         ),
  //     },
  //   ],
  // },
];
