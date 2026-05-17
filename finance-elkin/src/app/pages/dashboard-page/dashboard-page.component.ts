import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet],
  template: `
    <div class="flex h-screen w-full bg-background overflow-hidden">
      
      <!-- Sidebar -->
      <aside class="w-64 flex flex-col bg-sidebar border-r border-sidebar-border text-sidebar-foreground shadow-sm z-10 transition-all duration-300">
        <!-- Logo / Brand Area -->
        <div class="h-16 flex items-center px-6 border-b border-sidebar-border/50">
          <div class="flex items-center gap-2">
            <div class="h-8 w-8 rounded-md bg-sidebar-primary flex items-center justify-center text-sidebar-primary-foreground font-bold">
              P
            </div>
            <span class="font-bold text-lg tracking-tight">PROSPERA</span>
          </div>
        </div>

        <!-- Navigation -->
        <nav class="flex-1 overflow-y-auto py-4 px-3 space-y-1">
          <!-- Ejemplo de enlace de navegación (Se llenará en la fase correspondiente) -->
          <a routerLink="/dashboard" 
             routerLinkActive="bg-sidebar-primary text-sidebar-primary-foreground" 
             [routerLinkActiveOptions]="{exact: true}"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium hover:bg-sidebar-accent hover:text-sidebar-accent-foreground transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="7" height="9" x="3" y="3" rx="1"/><rect width="7" height="5" x="14" y="3" rx="1"/><rect width="7" height="9" x="14" y="12" rx="1"/><rect width="7" height="5" x="3" y="16" rx="1"/></svg>
            Resumen
          </a>
          
          <a routerLink="/dashboard/settings" 
             routerLinkActive="bg-sidebar-primary text-sidebar-primary-foreground"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground transition-colors mt-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>
            Configuración
          </a>

          <!-- Categoría Accesos -->
          <div class="px-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider mt-6 mb-2">Accesos</div>
          
          <a routerLink="/dashboard/users" 
             routerLinkActive="bg-sidebar-primary text-sidebar-primary-foreground"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground transition-colors mt-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            Usuarios
          </a>
          
          <a routerLink="/dashboard/roles" 
             routerLinkActive="bg-sidebar-primary text-sidebar-primary-foreground"
             class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground transition-colors mt-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg>
            Roles y Permisos
          </a>
        </nav>

        <!-- Sidebar Footer / Logout -->
        <div class="p-4 border-t border-sidebar-border/50">
          <button (click)="logout()" class="flex w-full items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground hover:bg-destructive hover:text-destructive-foreground transition-colors group">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-sidebar-foreground/70 group-hover:text-destructive-foreground transition-colors"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>
            Cerrar Sesión
          </button>
        </div>
      </aside>

      <!-- Main Content Area -->
      <main class="flex-1 flex flex-col min-w-0 overflow-hidden bg-muted/20">
        <!-- Header (Opcional) -->
        <header class="h-16 flex items-center justify-between px-8 bg-card border-b border-border shadow-sm">
          <h1 class="text-xl font-semibold text-foreground">Panel de Control</h1>
          <!-- User Profile / Notifications placeholder -->
          <div class="flex items-center gap-4">
             <div class="h-8 w-8 rounded-full bg-secondary text-secondary-foreground flex items-center justify-center font-medium text-sm">
               US
             </div>
          </div>
        </header>

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
