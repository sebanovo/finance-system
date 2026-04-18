import { DatePipe } from "@angular/common";
import { Component, input, output } from "@angular/core";
import { provideIcons } from "@ng-icons/core";
import { lucideAlertCircle } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmDialogImports } from "@shared/ui/dialog";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { AppHttpError } from "../../../../core/http/models/app-http-error.model";
import { Tenant } from "../../models/tenant.type";

@Component({
  selector: "app-tenant-detail-dialog",
  imports: [
    DatePipe,
    HlmAlertImports,
    HlmBadgeImports,
    HlmButtonImports,
    HlmCardImports,
    HlmDialogImports,
    HlmSkeletonImports,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
    }),
  ],
  templateUrl: "./tenant-detail-dialog.html",
  styleUrl: "./tenant-detail-dialog.css",
})
export class TenantDetailDialog {
  readonly open = input.required<boolean>();
  readonly tenant = input<Tenant | null>(null);
  readonly loading = input(false);
  readonly error = input<AppHttpError | null>(null);

  readonly openChange = output<boolean>();
  readonly editTenant = output<string>();

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);
  }

  onEditTenant(tenantId: string): void {
    this.editTenant.emit(tenantId);
  }

  onClosed(): void {
    this.openChange.emit(false);
  }
}
