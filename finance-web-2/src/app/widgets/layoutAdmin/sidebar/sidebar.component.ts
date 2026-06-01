import { Component, inject, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { LucideAngularModule } from 'lucide-angular';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
}

interface MenuGroup {
  id: string;
  label: string;
  icon: string;
  items: MenuItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, ],
  animations: [
    trigger('accordion', [
      state('open', style({ height: '*', opacity: 1, visibility: 'visible', margin: '4px 0' })),
      state('closed', style({ height: '0px', opacity: 0, visibility: 'hidden', margin: '0' })),
      transition('open <=> closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ]),
    trigger('rotateIcon', [
      state('open', style({ transform: 'rotate(180deg)' })),
      state('closed', style({ transform: 'rotate(0)' })),
      transition('open <=> closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ]),
    trigger('fadeOverlay', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0 }))
      ])
    ])
  ],
  template: `
    <!-- Mobile Hamburger Button (Floating top-left when sidebar is closed) -->
    <button 
      *ngIf="!isMobileOpen"
      (click)="toggleMobileMenu()"
      class="md:hidden fixed top-3 left-4 z-40 p-2 bg-white border border-[#C8E6C9] rounded-md shadow-sm text-[#2E7D32] hover:bg-[#F1F8E9] transition-colors"
    >
      <lucide-icon name="menu" class="h-5 w-5"></lucide-icon>
    </button>

    <!-- Mobile Overlay Backdrop -->
    <div 
      *ngIf="isMobileOpen" 
      @fadeOverlay
      (click)="closeMobileMenu()"
      class="md:hidden fixed inset-0 bg-black/50 backdrop-blur-sm z-40"
    ></div>

    <!-- Sidebar Container -->
    <aside 
      class="fixed inset-y-0 left-0 z-50 flex h-full w-64 flex-col border-r border-[#C8E6C9] bg-white shadow-xl transition-transform duration-300 ease-in-out md:relative md:translate-x-0 md:shadow-none"
      [class.-translate-x-full]="!isMobileOpen"
      [class.translate-x-0]="isMobileOpen"
    >
      <!-- Logo / Brand Area -->
      <div class="flex h-16 shrink-0 items-center justify-between border-b border-[#C8E6C9] px-6">
        <div class="flex items-center gap-3">
          <div class="flex h-8 w-8 items-center justify-center rounded-md bg-[#2E7D32] font-bold text-white shadow-sm">
            F
          </div>
          <span class="font-bold text-lg tracking-tight text-[#2E7D32]">Finance System</span>
        </div>
        
        <!-- Mobile Close Button -->
        <button (click)="closeMobileMenu()" class="md:hidden p-1.5 text-[#666666] hover:text-[#2E7D32] rounded-md hover:bg-[#F1F8E9] transition-colors">
          <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="custom-scrollbar flex-1 space-y-1 overflow-y-auto px-3 py-4">
        
        <!-- ============================================================ -->
        <!-- MENÚ PARA PLATFORM (SuperAdmin) -->
        <!-- ============================================================ -->
        <ng-container *ngIf="isPlatformRoute">
          <a *ngFor="let item of platformMenuItems" 
             [routerLink]="item.route" 
             routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm" 
             [routerLinkActiveOptions]="{exact: true}"
             (click)="closeMobileMenu()"
             class="group flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-[#333333] transition-all duration-200 hover:bg-[#F1F8E9] hover:text-[#2E7D32]">
            <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
            {{ item.label }}
          </a>
        </ng-container>

        <!-- ============================================================ -->
        <!-- MENÚ PARA TENANT (Usuario normal) -->
        <!-- ============================================================ -->
        <ng-container *ngIf="!isPlatformRoute">
          
          <!-- Elementos Generales (Sin Acordeón) -->
          <a *ngFor="let item of generalItems" 
             [routerLink]="item.route" 
             routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm" 
             [routerLinkActiveOptions]="{exact: item.route === '/dashboard'}"
             (click)="closeMobileMenu()"
             class="group flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-[#333333] transition-all duration-200 hover:bg-[#F1F8E9] hover:text-[#2E7D32]">
            <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
            {{ item.label }}
          </a>

          <div class="h-4"></div>

          <!-- Grupos Acordeón -->
          <div *ngFor="let group of menuGroups" class="mb-2">
            
            <!-- Header del Grupo -->
            <button 
              (click)="toggleGroup(group.id)"
              class="group flex w-full items-center justify-between rounded-md px-3 py-2 text-sm font-medium text-[#333333] transition-colors hover:bg-[#F1F8E9] hover:text-[#2E7D32]"
            >
              <div class="flex items-center gap-3">
                <lucide-icon [name]="group.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
                <span>{{ group.label }}</span>
              </div>
              <lucide-icon 
                name="chevron-down" 
                class="h-4 w-4 opacity-50"
                [@rotateIcon]="expandedGroups[group.id] ? 'open' : 'closed'"
              ></lucide-icon>
            </button>

            <!-- Contenido del Grupo -->
            <div [@accordion]="expandedGroups[group.id] ? 'open' : 'closed'" class="overflow-hidden px-2">
              <div class="ml-2 flex flex-col space-y-1 border-l border-[#C8E6C9] py-1 pl-4">
                <a *ngFor="let item of group.items" 
                   [routerLink]="item.route" 
                   routerLinkActive="text-[#2E7D32] font-semibold bg-[#E8F5E9]"
                   (click)="closeMobileMenu()"
                   class="flex items-center gap-3 rounded-md px-3 py-2 text-sm text-[#666666] transition-all duration-200 hover:bg-[#F1F8E9] hover:text-[#2E7D32]">
                  <lucide-icon [name]="item.icon" class="h-3.5 w-3.5 opacity-60"></lucide-icon>
                  {{ item.label }}
                </a>
              </div>
            </div>
            
          </div>
        </ng-container>

      </nav>

      <!-- Sidebar Footer / Logout -->
      <div class="shrink-0 border-t border-[#C8E6C9] p-4">
        <button 
          (click)="onLogout()" 
          class="group flex w-full items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-[#666666] transition-all duration-200 hover:bg-red-50 hover:text-red-600"
        >
          <lucide-icon name="log-out" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
          Cerrar Sesión
        </button>
      </div>
    </aside>
  `,
  styles: [`
    .custom-scrollbar::-webkit-scrollbar {
      width: 4px;
    }
    .custom-scrollbar::-webkit-scrollbar-track {
      background: transparent;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: rgba(0,0,0,0.1);
      border-radius: 10px;
    }
  `]
})
export class SidebarComponent {
  @Output() logoutAction = new EventEmitter<void>();
  
  private router = inject(Router);

  isMobileOpen = false;

  // Estado de los acordeones (por defecto Operaciones abierto)
  expandedGroups: Record<string, boolean> = {
    accesos: false,
    operaciones: true,
    divisas: false,
    seguridad: false,
    contabilidad: false
  };

  // ============================================================
  // MENÚ PARA TENANT
  // ============================================================
  generalItems: MenuItem[] = [
    { label: 'Panel de Resumen', route: '/dashboard', icon: 'layout-dashboard' },
    { label: 'Configuración', route: '/dashboard/settings', icon: 'settings' }
  ];

  menuGroups: MenuGroup[] = [
    {
      id: 'accesos',
      label: 'Accesos y Control',
      icon: 'shield',
      items: [
        { label: 'Usuarios', route: '/dashboard/users', icon: 'users' },
        { label: 'Roles y Permisos', route: '/dashboard/roles', icon: 'key' }
      ]
    },
    {
      id: 'operaciones',
      label: 'Operaciones',
      icon: 'briefcase',
      items: [
        { label: 'Cuentas Bancarias', route: '/dashboard/accounts', icon: 'credit-card' },
        { label: 'Transacciones', route: '/dashboard/transactions', icon: 'arrow-right-left' }
      ]
    },
    {
      id: 'divisas',
      label: 'Divisas y Tarifas',
      icon: 'dollar-sign',
      items: [
        { label: 'Tipos de Cambio', route: '/dashboard/fx/rates', icon: 'refresh-ccw' },
        { label: 'Comisiones', route: '/dashboard/fx/fees', icon: 'percent' }
      ]
    },
    {
      id: 'seguridad',
      label: 'Seguridad',
      icon: 'lock',
      items: [
        { label: 'Límites Operativos', route: '/dashboard/limits/rules', icon: 'shield-alert' }
      ]
    },
    {
      id: 'contabilidad',
      label: 'Contabilidad',
      icon: 'book-open',
      items: [
        { label: 'Períodos', route: '/dashboard/accounting/periods', icon: 'calendar' },
        { label: 'Asientos', route: '/dashboard/accounting/journal-entries', icon: 'file-text' }
      ]
    }
  ];

  // ============================================================
  // MENÚ PARA PLATFORM (SuperAdmin)
  // ============================================================
  platformMenuItems: MenuItem[] = [
    { label: 'Dashboard', route: '/platform/dashboard', icon: 'layout-dashboard' },
    { label: 'Mi perfil', route: '/platform/profile', icon: 'user-circle-2' },
    { label: 'Seguridad', route: '/platform/security', icon: 'lock' },
    { label: 'Respaldos', route: '/platform/backups', icon: 'arrow-down-to-line' },
    { label: 'Planes', route: '/platform/plans', icon: 'credit-card' },
    { label: 'Tenants', route: '/platform/tenants', icon: 'building-2' },
    { label: 'Suscripciones', route: '/platform/subscriptions', icon: 'dollar-sign' },
    { label: 'Auditoría', route: '/platform/audit', icon: 'clipboard-list' }
  ];

  // ============================================================
  // DETECCIÓN DE RUTA
  // ============================================================
  get isPlatformRoute(): boolean {
    return this.router.url.startsWith('/platform');
  }

  // ============================================================
  // MÉTODOS
  // ============================================================
  toggleGroup(id: string): void {
    this.expandedGroups[id] = !this.expandedGroups[id];
  }

  toggleMobileMenu(): void {
    this.isMobileOpen = !this.isMobileOpen;
  }

  closeMobileMenu(): void {
    this.isMobileOpen = false;
  }

  onLogout(): void {
    this.logoutAction.emit();
  }
}
