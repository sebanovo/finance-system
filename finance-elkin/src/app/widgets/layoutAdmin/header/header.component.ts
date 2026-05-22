import { Component, EventEmitter, Output, HostListener, ElementRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthFacade } from '../../../shared/lib/auth/auth.facade';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  animations: [
    trigger('dropdownAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }),
        animate('150ms cubic-bezier(0.4, 0, 0.2, 1)', style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('150ms cubic-bezier(0.4, 0, 0.2, 1)', style({ opacity: 0, transform: 'scale(0.95) translateY(-10px)' }))
      ])
    ])
  ],
  template: `
    <!-- Spacer for Mobile so the title doesn't hide behind the hamburger button -->
    <header class="h-16 flex items-center justify-between px-6 md:px-8 bg-card border-b border-border shadow-sm w-full sticky top-0 z-30">
      
      <!-- Lado Izquierdo -->
      <div class="flex items-center gap-4">
        <!-- En móvil, el sidebar tiene un botón en el top-left. Así que damos un margen izquierdo extra al título en móvil. -->
        <h1 class="text-xl font-semibold text-foreground ml-10 md:ml-0 tracking-tight">Panel de Control</h1>
      </div>

      <!-- Lado Derecho (Notificaciones + Perfil) -->
      <div class="flex items-center gap-2 md:gap-4 relative">
        
        <!-- Notificaciones -->
        <button class="relative p-2 text-muted-foreground hover:text-foreground hover:bg-accent rounded-full transition-colors">
          <lucide-icon name="bell" class="h-5 w-5"></lucide-icon>
          <span class="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-destructive border-2 border-card"></span>
        </button>

        <div class="h-6 w-px bg-border mx-1"></div>

        <!-- Perfil Usuario -->
        <div class="relative">
          <button 
            (click)="toggleProfileMenu()"
            class="flex items-center gap-2 md:gap-3 p-1.5 rounded-full md:rounded-md hover:bg-accent transition-colors focus:outline-none"
            [class.bg-accent]="isProfileOpen"
          >
            <!-- Avatar -->
            <div class="h-8 w-8 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-sm border border-primary/20 uppercase">
              {{ getInitials() }}
            </div>
            <!-- Info Desktop -->
            <div class="hidden md:flex flex-col items-start text-left">
              <span class="text-sm font-semibold text-foreground leading-none">
                {{ user() ? (user()!.firstName + ' ' + user()!.lastName) : 'Cargando...' }}
              </span>
              <span class="text-xs text-muted-foreground mt-1 capitalize">
                {{ getRole() }}
              </span>
            </div>
            <!-- Chevron -->
            <lucide-icon 
              name="chevron-down" 
              class="hidden md:block h-4 w-4 text-muted-foreground transition-transform duration-200"
              [class.rotate-180]="isProfileOpen"
            ></lucide-icon>
          </button>

          <!-- Dropdown Menu -->
          <div 
            *ngIf="isProfileOpen"
            @dropdownAnimation
            class="absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-card border border-border ring-1 ring-black/5 focus:outline-none z-50 overflow-hidden"
          >
            <!-- Header Dropdown (Mobile visible info) -->
            <div class="px-4 py-3 border-b border-border bg-muted/30">
              <p class="text-sm font-medium text-foreground">
                {{ user() ? (user()!.firstName + ' ' + user()!.lastName) : 'Usuario' }}
              </p>
              <p class="text-xs font-medium text-muted-foreground truncate">
                {{ user()?.email || 'Cargando...' }}
              </p>
            </div>

            <div class="py-1">
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-foreground hover:bg-accent hover:text-accent-foreground transition-colors">
                <lucide-icon name="user" class="mr-3 h-4 w-4 text-muted-foreground group-hover:text-foreground transition-colors"></lucide-icon>
                Mi Perfil
              </a>
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-foreground hover:bg-accent hover:text-accent-foreground transition-colors">
                <lucide-icon name="settings" class="mr-3 h-4 w-4 text-muted-foreground group-hover:text-foreground transition-colors"></lucide-icon>
                Configuración
              </a>
            </div>

            <div class="border-t border-border py-1">
              <button 
                (click)="onLogout()"
                class="group flex w-full items-center px-4 py-2 text-sm text-destructive hover:bg-destructive/10 transition-colors"
              >
                <lucide-icon name="log-out" class="mr-3 h-4 w-4 text-destructive/70 group-hover:text-destructive transition-colors"></lucide-icon>
                Cerrar Sesión
              </button>
            </div>
          </div>
        </div>

      </div>
    </header>
  `
})
export class HeaderComponent implements OnInit {
  @Output() logoutAction = new EventEmitter<void>();
  isProfileOpen = false;

  private elementRef = inject(ElementRef);
  private authFacade = inject(AuthFacade);

  readonly user = this.authFacade.currentUser;

  ngOnInit(): void {
    if (!this.user()) {
      this.authFacade.loadCurrentUser();
    }
  }

  getInitials(): string {
    const u = this.user();
    if (!u) return 'US';
    return (u.firstName.charAt(0) + u.lastName.charAt(0)).toUpperCase();
  }

  getRole(): string {
    const roles = this.user()?.roles;
    if (!roles || roles.length === 0) return 'Usuario';
    // Removemos el prefijo ROLE_ si existe y capitalizamos
    const role = roles[0].replace('ROLE_', '').toLowerCase();
    return role;
  }

  toggleProfileMenu(): void {
    this.isProfileOpen = !this.isProfileOpen;
  }

  // Cerrar el dropdown si se hace clic afuera
  @HostListener('document:click', ['$event.target'])
  onClickOutside(targetElement: EventTarget | null): void {
    if (!targetElement) return;
    const clickedInside = this.elementRef.nativeElement.contains(targetElement as Node);
    if (!clickedInside) {
      this.isProfileOpen = false;
    }
  }

  onLogout(): void {
    this.isProfileOpen = false;
    this.logoutAction.emit();
  }
}
