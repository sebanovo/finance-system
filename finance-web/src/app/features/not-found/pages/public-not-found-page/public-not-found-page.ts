import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideArrowRight,
  lucideHouse,
  lucideSearchX,
  lucideShieldCheck,
} from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { NotFoundBrandPanel } from "../../components/not-found-brand-panel/not-found-brand-panel";
import { NotFoundHero } from "../../components/not-found-hero/not-found-hero";

@Component({
  selector: "app-public-not-found-page",
  imports: [
    RouterLink,
    HlmButtonImports,
    NgIcon,
    NotFoundHero,
    NotFoundBrandPanel,
  ],
  providers: [
    provideIcons({
      lucideSearchX,
      lucideHouse,
      lucideArrowRight,
      lucideShieldCheck,
    }),
  ],
  templateUrl: "./public-not-found-page.html",
  styleUrl: "./public-not-found-page.css",
})
export class PublicNotFoundPage {}
