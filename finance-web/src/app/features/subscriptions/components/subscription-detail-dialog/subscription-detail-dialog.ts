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
import { Subscription } from "../../models/subscription.type";

@Component({
  selector: "app-subscription-detail-dialog",
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
  host: {
    style: "display: block",
  },
  templateUrl: "./subscription-detail-dialog.html",
  styleUrl: "./subscription-detail-dialog.css",
})
export class SubscriptionDetailDialog {
  readonly open = input.required<boolean>();
  readonly subscription = input<Subscription | null>(null);
  readonly loading = input(false);
  readonly error = input<AppHttpError | null>(null);

  readonly openChange = output<boolean>();

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);
  }

  onClosed(): void {
    this.openChange.emit(false);
  }
}
