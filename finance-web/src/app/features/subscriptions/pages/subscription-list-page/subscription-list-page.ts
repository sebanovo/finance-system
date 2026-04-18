import { Component, inject, OnInit, signal } from "@angular/core";
import { provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucideLayers3,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { CardHeader } from "../../../../shared/custom-components/card-header/card-header";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { TableError } from "../../../../shared/custom-components/table-error/table-error";
import { SubscriptionDetailDialog } from "../../components/subscription-detail-dialog/subscription-detail-dialog";
import { SubscriptionTable } from "../../components/subscription-table/subscription-table";
import { Subscription } from "../../models/subscription.type";
import { SubscriptionsStore } from "../../store/subscription.store";

@Component({
  selector: "app-subscription-list-page",
  imports: [
    SubscriptionTable,
    SubscriptionDetailDialog,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    CardHeader,
    TableError,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucideLayers3,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./subscription-list-page.html",
  styleUrl: "./subscription-list-page.css",
})
export class SubscriptionListPage implements OnInit {
  readonly store = inject(SubscriptionsStore);
  readonly viewDialogOpen = signal(false);

  ngOnInit(): void {
    void this.store.loadSubscriptions();
  }

  onRetry(): void {
    void this.store.reloadSubscriptions();
  }

  async onViewSubscription(subscription: Subscription): Promise<void> {
    this.store.clearSelectedSubscriptionError();
    this.store.clearSelectedSubscription();
    this.viewDialogOpen.set(true);

    const loadedSubscription = await this.store.loadSubscriptionById(
      subscription.id
    );

    if (!loadedSubscription) {
      toast("No se pudo cargar la suscripción", {
        description:
          this.store.selectedSubscriptionError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onViewDialogOpenChange(isOpen: boolean): void {
    this.viewDialogOpen.set(isOpen);

    if (!isOpen) {
      this.store.clearSelectedSubscriptionError();
      this.store.clearSelectedSubscription();
    }
  }
}
