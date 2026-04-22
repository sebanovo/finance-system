import { Location } from "@angular/common";
import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideArrowLeft,
  lucideLayoutDashboard,
  lucidePanelTopOpen,
  lucideTriangleAlert,
} from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { NotFoundBrandPanel } from "../../components/not-found-brand-panel/not-found-brand-panel";
import { NotFoundHero } from "../../components/not-found-hero/not-found-hero";

@Component({
  selector: "app-private-not-found-page",
  imports: [
    NgIcon,
    RouterLink,
    HlmButtonImports,
    NotFoundHero,
    NotFoundBrandPanel,
  ],
  providers: [
    provideIcons({
      lucideTriangleAlert,
      lucideArrowLeft,
      lucideLayoutDashboard,
      lucidePanelTopOpen,
    }),
  ],
  templateUrl: "./private-not-found-page.html",
  styleUrl: "./private-not-found-page.css",
})
export class PrivateNotFoundPage {
  private readonly location = inject(Location);

  goBack(): void {
    this.location.back();
  }
}
