import { Component, input } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBadgeCheck,
  lucideCalendarClock,
  lucideLayers3,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmCardImports } from "@shared/ui/card";
import { Subscription } from "../../models/subscription.type";

@Component({
  selector: "app-current-subscription-summary",
  imports: [NgIcon, HlmBadgeImports, HlmCardImports],
  providers: [
    provideIcons({
      lucideBadgeCheck,
      lucideCalendarClock,
      lucideLayers3,
      lucideUsers,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./current-subscription-summary.html",
  styleUrl: "./current-subscription-summary.css",
})
export class CurrentSubscriptionSummary {
  readonly subscription = input.required<Subscription>();
}
