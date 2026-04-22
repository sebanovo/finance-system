import { Routes } from "@angular/router";

export const PUBLIC_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/home-page/home-page").then(m => m.HomePage),
  },
  {
    path: "pricing",
    loadComponent: () =>
      import("./pages/pricing-page/pricing-page").then(m => m.PricingPage),
  },
  {
    path: "table-case",
    loadComponent: () =>
      import("./pages/table-case/table-case").then(m => m.TableCase),
  },

  {
    path: "media-lab",
    loadComponent: () =>
      import("../media-lab/pages/media-lab-page/media-lab-page").then(
        m => m.MediaLabPage
      ),
  },
];
