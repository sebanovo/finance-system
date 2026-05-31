// widgets/layoutAdmin/header/header.component.ts
import { Component, EventEmitter, Output, HostListener, ElementRef, inject, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthFacade } from '../../../shared/lib/auth/auth.facade';
import { PlatformFacade } from '../../../features/platform/lib/platform.facade';

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
    <header class="h-16 flex items-center justify-between px-6 md:px-8 bg-white border-b border-[#C8E6C9] shadow-sm w-full sticky top-0 z-30">
      
      <div class="flex items-center gap-4">
        <h1 class="text-xl font-semibold text-[#2E7D32] ml-10 md:ml-0 tracking-tight">
          @if (isPlatformRoute) {
            Panel de Control - Plataforma
          } @else {
            Panel de Control
          }
        </h1>
      </div>

      <div class="flex items-center gap-2 md:gap-4 relative">
        
        <div class="h-6 w-px bg-[#C8E6C9] mx-1"></div>

        <!-- Perfil Usuario (adaptado para Platform o Tenant) -->
        <div class="relative">
          <button 
            (click)="toggleProfileMenu()"
            class="flex items-center gap-2 md:gap-3 p-1.5 rounded-full md:rounded-md hover:bg-[#F1F8E9] transition-colors focus:outline-none"
            [class.bg-[#F1F8E9]]="isProfileOpen"
          >
            <!-- Avatar -->
            <div class="h-8 w-8 rounded-full bg-[#2E7D32]/10 text-[#2E7D32] flex items-center justify-center font-bold text-sm border border-[#2E7D32]/20 uppercase">
              {{ getInitials() }}
            </div>
            <!-- Info Desktop -->
            <div class="hidden md:flex flex-col items-start text-left">
              <span class="text-sm font-semibold text-[#2E7D32] leading-none">
                {{ getDisplayName() }}
              </span>
              <span class="text-xs text-[#666666] mt-1 capitalize">
                {{ getRole() }}
              </span>
            </div>
            <!-- Chevron -->
            <lucide-icon 
              name="chevron-down" 
              class="hidden md:block h-4 w-4 text-[#666666] transition-transform duration-200"
              [class.rotate-180]="isProfileOpen"
            ></lucide-icon>
          </button>

          <!-- Dropdown Menu -->
          <div 
            *ngIf="isProfileOpen"
            @dropdownAnimation
            class="absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white border border-[#C8E6C9] ring-1 ring-black/5 focus:outline-none z-50 overflow-hidden"
          >
            <!-- Header Dropdown -->
            <div class="px-4 py-3 border-b border-[#C8E6C9] bg-[#F1F8E9]">
              <p class="text-sm font-medium text-[#2E7D32]">
                {{ getDisplayName() }}
              </p>
              <p class="text-xs font-medium text-[#666666] truncate">
                {{ getEmail() }}
              </p>
            </div>

            <div class="py-1">
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-[#333333] hover:bg-[#F1F8E9] transition-colors">
                <lucide-icon name="user" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                Mi Perfil
              </a>
              <a routerLink="/dashboard/settings" class="group flex items-center px-4 py-2 text-sm text-[#333333] hover:bg-[#F1F8E9] transition-colors">
                <lucide-icon name="settings" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                Configuración
              </a>
            </div>

            <div class="border-t border-[#C8E6C9] py-1">
              <button 
                (click)="onLogout()"
                class="group flex w-full items-center px-4 py-2 text-sm text-[#C62828] hover:bg-red-50 transition-colors"
              >
                <lucide-icon name="log-out" class="mr-3 h-4 w-4 text-[#C62828]/70 group-hover:text-[#C62828]"></lucide-icon>
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
  private router = inject(Router);
  private authFacade = inject(AuthFacade);
  private platformFacade = inject(PlatformFacade);

  readonly platformSuperadmin = this.platformFacade.currentSuperadmin;
  readonly tenantUser = this.authFacade.currentUser;

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    if (this.isPlatformRoute) {
      // ✅ Cargar datos del superadmin de platform
      this.platformFacade.loadCurrentSuperadmin();
    } else {
      // ✅ Cargar datos del usuario tenant
      if (!this.tenantUser()) {
        this.authFacade.loadCurrentUser();
      }
    }
  }

  get isPlatformRoute(): boolean {
    return this.router.url.startsWith('/platform');
  }

  getDisplayName(): string {
    if (this.isPlatformRoute) {
      const user = this.platformSuperadmin();
      if (user) {
        return `${user.firstName} ${user.lastName}`;
      }
      return 'SuperAdmin';
    } else {
      const user = this.tenantUser();
      if (user) {
        return `${user.firstName} ${user.lastName}`;
      }
      return 'Cargando...';
    }
  }

  getEmail(): string {
    if (this.isPlatformRoute) {
      return this.platformSuperadmin()?.email || 'superadmin@finance.local';
    } else {
      return this.tenantUser()?.email || 'cargando...';
    }
  }

  getInitials(): string {
    if (this.isPlatformRoute) {
      const user = this.platformSuperadmin();
      if (user) {
        return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
      }
      return 'SA';
    } else {
      const user = this.tenantUser();
      if (user) {
        return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
      }
      return 'US';
    }
  }

  getRole(): string {
    if (this.isPlatformRoute) {
      return 'Super Administrador';
    } else {
      const roles = this.tenantUser()?.roles;
      if (!roles || roles.length === 0) return 'Usuario';
      const role = roles[0].replace('ROLE_', '').toLowerCase();
      return role;
    }
  }

  toggleProfileMenu(): void {
    this.isProfileOpen = !this.isProfileOpen;
  }

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