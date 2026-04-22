import { Component, inject, OnInit } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideAlertCircle, lucideCreditCard } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { CurrentSubscriptionSummary } from "../../components/current-subscription-summary/current-subscription-summary";
import { SubscriptionsStore } from "../../store/subscription.store";

@Component({
  selector: "app-current-subscription-page",
  imports: [
    CurrentSubscriptionSummary,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    NgIcon,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideCreditCard,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./current-subscription-page.html",
  styleUrl: "./current-subscription-page.css",
})
export class CurrentSubscriptionPage implements OnInit {
  readonly store = inject(SubscriptionsStore);

  ngOnInit(): void {
    void this.store.loadCurrentSubscription();
  }

  onRetry(): void {
    void this.store.loadCurrentSubscription();
  }
}
