import { inject } from "@angular/core";
import { ResolveFn } from "@angular/router";
import { TenantService } from "../services/tenant.service";

export const tenantListResolver: ResolveFn<any[]> = () => {
  const service = inject(TenantService);
  return service.getTenants();
};
