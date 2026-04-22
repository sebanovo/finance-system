import { Routes } from "@angular/router";

export const SETTINGS_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/tenant-setting-page/tenant-setting-page").then(
        m => m.TenantSettingPage
      ),
  },
];
