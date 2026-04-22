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
import { User } from "../../models/user.model";

@Component({
  selector: "app-user-detail-dialog",
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
  templateUrl: "./user-detail-dialog.html",
  styleUrl: "./user-detail-dialog.css",
})
export class UserDetailDialog {
  readonly open = input.required<boolean>();
  readonly user = input<User | null>(null);
  readonly loading = input(false);
  readonly error = input<AppHttpError | null>(null);

  readonly openChange = output<boolean>();
  readonly editUser = output<string>();

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);
  }

  onEditUser(userId: string): void {
    this.editUser.emit(userId);
  }

  onClosed(): void {
    this.openChange.emit(false);
  }
}
