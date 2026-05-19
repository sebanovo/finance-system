import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';
import { SidebarComponent, HeaderComponent } from '../../widgets/layoutAdmin';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, SidebarComponent, HeaderComponent],
  template: `
    <div class="flex h-screen w-full bg-background overflow-hidden">
      
      <!-- Sidebar Separado como Widget. El w-0 md:w-64 es crucial para que el contenedor reserve el espacio del Sidebar y no desalinee el header -->
      <app-sidebar class="w-0 md:w-64 shrink-0 transition-all duration-300 relative z-50" (logoutAction)="logout()"></app-sidebar>

      <!-- Main Content Area -->
      <main class="flex-1 flex flex-col min-w-0 overflow-hidden bg-muted/20 relative">
        
        <!-- Header Separado como Widget -->
        <app-header (logoutAction)="logout()"></app-header>

        <!-- Content Area -->
        <div class="flex-1 overflow-y-auto p-8">
          <router-outlet></router-outlet>
        </div>
      </main>

    </div>
  `
})
export class DashboardPageComponent {
  private readonly authFacade = inject(AuthFacade);

  logout(): void {
    this.authFacade.logout();
  }
}
