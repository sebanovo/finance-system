// widgets/layoutAdmin/header/header.component.ts
import { Component, EventEmitter, Output, HostListener, ElementRef, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthFacade } from '../../../shared/lib/auth/auth.facade';
import { PlatformFacade } from '../../../features/platform/lib/platform.facade';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

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
    <header class="sticky top-0 z-30 flex h-16 w-full items-center justify-between border-b border-[#C8E6C9] bg-white px-6 shadow-sm md:px-8">
      
      <div class="flex items-center gap-4">
        <h1 class="ml-10 text-xl font-semibold tracking-tight text-[#2E7D32] md:ml-0">
          @if (isPlatformRoute) {
            Panel de Control - Plataforma
          } @else {
            Panel de Control - Tenant
          }
        </h1>
      </div>

      <div class="relative flex items-center gap-2 md:gap-4">
        
        <div class="h-6 w-px bg-[#C8E6C9] mx-1"></div>

        <!-- Perfil Usuario (adaptado para Platform o Tenant) -->
        <div class="relative">
          <button 
            (click)="toggleProfileMenu()"
            class="flex cursor-pointer items-center gap-2 rounded-full p-1.5 transition-colors hover:bg-[#F1F8E9] focus:outline-none md:gap-3 md:rounded-md"
            [class.bg-[#F1F8E9]]="isProfileOpen"
          >
            <!-- Avatar -->
            <div class="flex h-8 w-8 items-center justify-center rounded-full border border-[#2E7D32]/20 bg-[#2E7D32]/10 text-sm font-bold uppercase text-[#2E7D32]">
              {{ getInitials() }}
            </div>
            <!-- Info Desktop -->
            <div class="hidden flex-col items-start text-left md:flex">
              <span class="text-sm font-semibold leading-none text-[#2E7D32]">
                {{ getDisplayName() }}
              </span>
              <span class="text-xs text-[#666666] mt-1 capitalize">
                {{ getRole() }}
              </span>
            </div>
            <!-- Chevron -->
            <lucide-icon 
              name="chevron-down" 
              class="hidden h-4 w-4 text-[#666666] transition-transform duration-200 md:block"
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
            @if (isPlatformRoute) {
                @for (item of platformMenuOptions; track item.route) {
                  <a
                    [routerLink]="item.route"
                    (click)="isProfileOpen = false"
                    class="group flex cursor-pointer items-center px-4 py-2 text-sm text-[#333333] transition-colors hover:bg-[#F1F8E9]">
                    <lucide-icon [name]="item.icon" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                    {{ item.label }}
                  </a>
                }
              } @else {
                <a routerLink="/dashboard/summary" class="group flex cursor-pointer items-center px-4 py-2 text-sm text-[#333333] transition-colors hover:bg-[#F1F8E9]">
                  <lucide-icon name="layout-dashboard" class="mr-3 h-4 w-4 text-[#666666] group-hover:text-[#2E7D32]"></lucide-icon>
                  Resumen
                </a>
              }
            </div>

            <div class="border-t border-[#C8E6C9] py-1">
              <button 
                (click)="onLogout()"
                class="group flex w-full cursor-pointer items-center px-4 py-2 text-sm text-[#C62828] transition-colors hover:bg-red-50"
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

  readonly platformMenuOptions: MenuItem[] = [
    { label: 'Mi perfil', route: '/platform/profile', icon: 'user-circle-2' },
    { label: 'Seguridad', route: '/platform/security', icon: 'lock' },
    { label: 'Respaldos', route: '/platform/backups', icon: 'arrow-down-to-line' }
  ];

  private elementRef = inject(ElementRef);
  private router = inject(Router);
  private authFacade = inject(AuthFacade);
  private platformFacade = inject(PlatformFacade);

  readonly platformSuperadmin = this.platformFacade.currentSuperadmin;
  readonly tenantUser = this.authFacade.currentUser;

  ngOnInit(): void {
    if (this.isPlatformRoute) {
      this.platformFacade.loadCurrentSuperadmin();
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
