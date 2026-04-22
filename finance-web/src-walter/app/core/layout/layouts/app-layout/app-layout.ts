import {
  ChangeDetectionStrategy,
  Component,
  HostListener,
  inject,
} from "@angular/core";
import { Router, RouterOutlet } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideChevronLeft, lucideChevronRight } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { AppHeader } from "../../components/app-header/app-header";
import { AppSidebar } from "../../components/app-sidebar/app-sidebar";
import { AppLayoutState } from "../../services/app-layout.state";

@Component({
  selector: "app-layout",
  standalone: true,
  imports: [RouterOutlet, AppSidebar, AppHeader, NgIcon, HlmButtonImports],
  providers: [provideIcons({ lucideChevronLeft, lucideChevronRight })],
  templateUrl: "./app-layout.html",
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
