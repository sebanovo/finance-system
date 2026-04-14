import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
} from "@angular/core";
import { Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBell,
  lucidePanelLeft,
  lucidePanelLeftClose,
  lucideSearch,
  lucideUser,
} from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmInputImports } from "@shared/ui/input";
import { SessionStore } from "../../../session/store/session.store";
import { AppLayoutState } from "../../services/app-layout.state";

@Component({
  selector: "app-app-header",
  standalone: true,
  imports: [NgIcon, HlmButtonImports, HlmInputImports],
  providers: [
    provideIcons({
      lucideBell,
      lucidePanelLeft,
      lucidePanelLeftClose,
      lucideSearch,
      lucideUser,
    }),
  ],
  templateUrl: "./app-header.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppHeader {
  private sessionStore = inject(SessionStore);
  private router = inject(Router);

  readonly title = input("Dashboard");
  readonly subtitle = input("Administración");
  readonly showSearch = input(true);
  readonly showNotifications = input(true);
  readonly showProfile = input(true);
  readonly searchPlaceholder = input("Buscar...");

  protected readonly layoutState = inject(AppLayoutState);

  protected toggleDesktopSidebar(): void {
    this.layoutState.toggleSidebarCollapsed();
  }

  protected openMobileSidebar(): void {
    this.layoutState.openMobileSidebar();
  }

  onClickPerfil(): void {
    this.sessionStore.clearSession();
    void this.router.navigateByUrl("/auth/login");
    // this.logoutService.logout();
    // void this.router.navigate([""]);
  }
}
