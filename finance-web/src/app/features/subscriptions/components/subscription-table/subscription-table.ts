import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye } from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmTableImports } from "@shared/ui/table";
import { Subscription } from "../../models/subscription.type";

@Component({
  selector: "app-subscription-table",
  imports: [HlmTableImports, HlmBadgeImports, HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucideEye })],
  host: {
    style: "display: block",
  },
  templateUrl: "./subscription-table.html",
  styleUrl: "./subscription-table.css",
})
export class SubscriptionTable {
  readonly subscriptions = input.required<Subscription[]>();

  readonly viewSubscription = output<Subscription>();

  onViewSubscription(subscription: Subscription): void {
    this.viewSubscription.emit(subscription);
  }
}
