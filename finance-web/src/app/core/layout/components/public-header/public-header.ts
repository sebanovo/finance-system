import { Component, inject } from "@angular/core";
import { Router } from "@angular/router";
import { HlmButton } from "@shared/ui/button";
import {
  PUBLIC_NAVIGATION,
  PublicNavigationItem,
} from "../../constants/public-navigation.constants";

@Component({
  selector: "app-public-header",
  standalone: true,
  imports: [HlmButton],
  templateUrl: "./public-header.html",
  styleUrl: "./public-header.css",
})
export class PublicHeader {
  private readonly router = inject(Router);

  readonly navigationItems = PUBLIC_NAVIGATION;

  async onLogoClick(): Promise<void> {
    await this.navigateToSection("hero");
  }

  async onNavigationItemClick(item: PublicNavigationItem): Promise<void> {
    if (item.type === "page" && item.route) {
      await this.router.navigateByUrl(item.route);
      return;
    }

    if (item.type === "section" && item.fragment) {
      await this.navigateToSection(item.fragment);
    }
  }

  async onLoginClick(): Promise<void> {
    await this.router.navigateByUrl("/auth/login");
  }

  async onSignupClick(): Promise<void> {
    await this.router.navigateByUrl("/auth/signup");
  }

  async onGetStartedClick(): Promise<void> {
    await this.router.navigateByUrl("/pricing");
  }

  private async navigateToSection(sectionId: string): Promise<void> {
    const isHomePage =
      this.router.url === "/" || this.router.url.startsWith("/#");

    if (!isHomePage) {
      await this.router.navigate(["/"]);
      this.scrollToSection(sectionId);
      return;
    }

    this.scrollToSection(sectionId);
  }

  private scrollToSection(sectionId: string): void {
    requestAnimationFrame(() => {
      const element = document.getElementById(sectionId);

      if (!element) {
        return;
      }

      element.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    });
  }
}
