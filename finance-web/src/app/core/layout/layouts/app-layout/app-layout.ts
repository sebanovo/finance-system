import {
  ChangeDetectionStrategy,
  Component,
  HostListener,
  inject,
} from "@angular/core";
import { Router, RouterOutlet } from "@angular/router";
import { AppHeader } from "../../components/app-header/app-header";
import { AppSidebar } from "../../components/app-sidebar/app-sidebar";
import { AppLayoutState } from "../../services/app-layout.state";
// import { AppHeaderComponent } from "../../components/app-header/app-header.component";
// import { AppSidebarComponent } from "../../components/app-sidebar/app-sidebar.component";
// import { AppLayoutState } from "../../state/app-layout.state";

@Component({
  selector: "app-layout",
  standalone: true,
  imports: [RouterOutlet, AppSidebar, AppHeader],
  template: `
    <div class="min-h-screen bg-slate-50 text-slate-900">
      <div class="flex min-h-screen">
        <app-app-sidebar [collapsed]="layoutState.sidebarCollapsed()" />

        <main class="flex min-h-screen min-w-0 flex-1 flex-col overflow-hidden">
          <app-app-header
            title="Dashboard"
            subtitle="Administración"
            [showSearch]="true"
            [showNotifications]="true"
            [showProfile]="true"
          />

          <section class="flex-1 overflow-x-hidden p-6">
            <router-outlet />
          </section>
        </main>
      </div>

      @if (layoutState.mobileSidebarOpen()) {
        <div class="fixed inset-0 z-40 lg:hidden">
          <button
            type="button"
            class="absolute inset-0 bg-slate-950/40"
            aria-label="Cerrar menú"
            (click)="layoutState.closeMobileSidebar()"
          ></button>

          <div class="absolute inset-y-0 left-0 z-50 w-72 max-w-[85vw]">
            <app-app-sidebar [collapsed]="false" [mobile]="true" />
          </div>
        </div>
      }
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppLayout {
  protected readonly layoutState = inject(AppLayoutState);

  private readonly router = inject(Router);

  constructor() {
    this.router.events.subscribe(() => {
      this.layoutState.closeMobileSidebar();
    });
  }

  @HostListener("window:keydown.escape")
  protected onEscape(): void {
    this.layoutState.closeMobileSidebar();
  }
}

// import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
// import { RouterOutlet } from "@angular/router";
// import { AppHeader } from "../../components/app-header/app-header";
// import { AppSidebar } from "../../components/app-sidebar/app-sidebar";
// import { AppLayoutState } from "../../services/app-layout.state";

// @Component({
//   selector: "app-app-layout",
//   standalone: true,
//   imports: [RouterOutlet, AppHeader, AppSidebar],
//   templateUrl: "./app-layout.html",
//   styleUrl: "./app-layout.css",
//   changeDetection: ChangeDetectionStrategy.OnPush,
// })
// export class AppLayout {
//   protected readonly layoutState = inject(AppLayoutState);
// }
