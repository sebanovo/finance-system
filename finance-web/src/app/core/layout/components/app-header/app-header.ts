import { AsyncPipe } from "@angular/common";
import { Component, inject } from "@angular/core";
import { NavigationEnd, Router } from "@angular/router";
import { filter, map, startWith } from "rxjs";
import {
  APP_PAGE_TITLES,
  AppPageTitleItem,
} from "../../constants/app-page-titles.constants";

@Component({
  selector: "app-app-header",
  imports: [AsyncPipe],
  templateUrl: "./app-header.html",
  styleUrl: "./app-header.css",
})
export class AppHeader {
  private readonly router = inject(Router);

  readonly currentPageInfo$ = this.router.events.pipe(
    filter(event => event instanceof NavigationEnd),
    startWith(null),
    map(() => this.getCurrentPageInfo(this.router.url))
  );

  onOpenMobileMenu(): void {
    console.log("Abrir menú móvil");
  }

  onSearchClick(): void {
    console.log("Abrir búsqueda");
  }

  onProfileClick(): void {
    void this.router.navigateByUrl("/settings");
  }

  private getCurrentPageInfo(url: string): AppPageTitleItem {
    const cleanUrl = this.normalizeUrl(url);

    return (
      APP_PAGE_TITLES.find(item => item.route === cleanUrl) ?? {
        route: cleanUrl,
        title: "Panel",
        subtitle: "Área privada",
      }
    );
  }

  private normalizeUrl(url: string): string {
    if (!url) {
      return "/dashboard";
    }

    const [path] = url.split("?");
    const [cleanPath] = path.split("#");

    return cleanPath || "/dashboard";
  }
}
