import { Routes } from "@angular/router";

export const AUDIT_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./pages/audit-page/audit-page").then(m => m.AuditPage),
  },
];
