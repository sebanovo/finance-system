import { computed, Injectable, signal } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class AppLayoutState {
  private readonly sidebarCollapsedSignal = signal(false);
  private readonly mobileSidebarOpenSignal = signal(false);

  readonly sidebarCollapsed = computed(() => this.sidebarCollapsedSignal());
  readonly mobileSidebarOpen = computed(() => this.mobileSidebarOpenSignal());

  toggleSidebarCollapsed(): void {
    this.sidebarCollapsedSignal.update(value => !value);
  }

  collapseSidebar(): void {
    this.sidebarCollapsedSignal.set(true);
  }

  expandSidebar(): void {
    this.sidebarCollapsedSignal.set(false);
  }

  openMobileSidebar(): void {
    this.mobileSidebarOpenSignal.set(true);
  }

  closeMobileSidebar(): void {
    this.mobileSidebarOpenSignal.set(false);
  }

  setMobileSidebarOpen(value: boolean): void {
    this.mobileSidebarOpenSignal.set(value);
  }
}
