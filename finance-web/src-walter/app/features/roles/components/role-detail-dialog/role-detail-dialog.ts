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
import { Role } from "../../model/role.type";

@Component({
  selector: "app-role-detail-dialog",
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
  templateUrl: "./role-detail-dialog.html",
  styleUrl: "./role-detail-dialog.css",
})
export class RoleDetailDialog {
  readonly open = input.required<boolean>();
  readonly role = input<Role | null>(null);
  readonly loading = input(false);
  readonly error = input<AppHttpError | null>(null);

  readonly openChange = output<boolean>();
  readonly editRole = output<string>();

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);
  }

  onEditRole(roleId: string): void {
    this.editRole.emit(roleId);
  }

  onClosed(): void {
    this.openChange.emit(false);
  }
}
