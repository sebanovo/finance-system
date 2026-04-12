import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
} from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideSettings, lucideUsers } from "@ng-icons/lucide";
import {
  remixBarChartBoxLine,
  remixBuildingLine,
  remixDashboardLine,
  remixFileList3Line,
  remixPriceTag3Line,
  remixShieldKeyholeLine,
  remixVipCrownLine,
} from "@ng-icons/remixicon";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSeparatorImports } from "@shared/ui/separator";
import { HlmTooltipImports } from "@shared/ui/tooltip";
import { APP_NAVIGATION } from "../../constants/app-navigation.constants";
import {
  AppNavigationItem,
  AppNavigationLinkItem,
} from "../../models/navigation.type";

@Component({
  selector: "app-app-sidebar",
  standalone: true,
  imports: [
    RouterLink,
    NgIcon,
    HlmButtonImports,
    HlmSeparatorImports,
    HlmTooltipImports,
  ],
  providers: [
    provideIcons({
      lucideSettings,
      lucideUsers,
      remixBarChartBoxLine,
      remixBuildingLine,
      remixDashboardLine,
      remixFileList3Line,
      remixPriceTag3Line,
      remixShieldKeyholeLine,
      remixVipCrownLine,
    }),
  ],
  templateUrl: "./app-sidebar.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppSidebar {
  readonly collapsed = input(false);
  readonly mobile = input(false);

  private readonly router = inject(Router);

  protected readonly navigation = computed(() => APP_NAVIGATION);

  protected isSection(
    item: AppNavigationItem
  ): item is Extract<AppNavigationItem, { type: "section" }> {
    return item.type === "section";
  }

  protected isActive(item: AppNavigationLinkItem): boolean {
    if (item.exact) {
      return this.router.url === item.route;
    }

    return (
      this.router.url === item.route ||
      this.router.url.startsWith(`${item.route}/`)
    );
  }

  protected resolveIconName(item: AppNavigationLinkItem): string {
    const iconMap: Record<string, string> = {
      "ri-dashboard-line": "remixDashboardLine",
      "ri-building-line": "remixBuildingLine",
      "ri-vip-crown-line": "remixVipCrownLine",
      users: "lucideUsers",
      "ri-bar-chart-box-line": "remixBarChartBoxLine",
      "ri-price-tag-3-line": "remixPriceTag3Line",
      "ri-shield-keyhole-line": "remixShieldKeyholeLine",
      "ri-file-list-3-line": "remixFileList3Line",
      settings: "lucideSettings",
    };

    return iconMap[item.icon.name];
  }

  protected trackByNavigationItem(
    index: number,
    item: AppNavigationItem
  ): string {
    if (this.isSection(item)) {
      return `section-${index}-${item.label}`;
    }

    return `link-${index}-${item.route}`;
  }
}
