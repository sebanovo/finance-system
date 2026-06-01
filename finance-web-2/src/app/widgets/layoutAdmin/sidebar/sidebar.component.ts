import { Component, inject, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { LucideAngularModule } from 'lucide-angular';
import { PermissionService } from '../../../shared/lib/auth/permission.service';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
  permissions?: string[];
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
  imports: [CommonModule, RouterModule, LucideAngularModule],
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
    <button 
      *ngIf="!isMobileOpen"
      (click)="toggleMobileMenu()"
      class="md:hidden fixed top-3 left-4 z-40 p-2 bg-white border border-[#C8E6C9] rounded-md shadow-sm text-[#2E7D32] hover:bg-[#F1F8E9] transition-colors"
    >
      <lucide-icon name="menu" class="h-5 w-5"></lucide-icon>
    </button>

    <div 
      *ngIf="isMobileOpen" 
      @fadeOverlay
      (click)="closeMobileMenu()"
      class="md:hidden fixed inset-0 bg-black/50 backdrop-blur-sm z-40"
    ></div>

    <aside 
      class="fixed inset-y-0 left-0 z-50 flex h-full w-64 flex-col border-r border-[#C8E6C9] bg-white shadow-xl transition-transform duration-300 ease-in-out md:relative md:translate-x-0 md:shadow-none"
      [class.-translate-x-full]="!isMobileOpen"
      [class.translate-x-0]="isMobileOpen"
    >
      <div class="flex h-16 shrink-0 items-center justify-between border-b border-[#C8E6C9] px-6">
        <div class="flex items-center gap-3">
          <div class="flex h-8 w-8 items-center justify-center rounded-md bg-[#2E7D32] font-bold text-white shadow-sm">
            F
          </div>
          <span class="font-bold text-lg tracking-tight text-[#2E7D32]">Finance System</span>
        </div>
        <button (click)="closeMobileMenu()" class="md:hidden p-1.5 text-[#666666] hover:text-[#2E7D32] rounded-md hover:bg-[#F1F8E9] transition-colors">
          <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
        </button>
      </div>

      <nav class="custom-scrollbar flex-1 space-y-1 overflow-y-auto px-3 py-4">
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

        <ng-container *ngIf="!isPlatformRoute">
          <a *ngFor="let item of generalItems"
             [routerLink]="item.route"
             routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm" 
             [routerLinkActiveOptions]="{exact: true}"
             (click)="closeMobileMenu()"
             class="group flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium text-[#333333] transition-all duration-200 hover:bg-[#F1F8E9] hover:text-[#2E7D32]">
            <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity"></lucide-icon>
            {{ item.label }}
          </a>

          <div class="h-4"></div>

          <div *ngFor="let group of menuGroups" class="mb-2">
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

            <div [@accordion]="expandedGroups[group.id] ? 'open' : 'closed'" class="overflow-hidden px-2">
              <div class="ml-2 flex flex-col space-y-1 border-l border-[#C8E6C9] py-1 pl-4">
                <a *ngFor="let item of group.items" 
                   [routerLink]="item.route" 
                   routerLinkActive="bg-[#2E7D32] text-white font-semibold shadow-sm"
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
  private readonly permissionService = inject(PermissionService);

  isMobileOpen = false;

  readonly isOwnerAdmin = this.permissionService.hasRole('OWNER_ADMIN');

  readonly dashboardRoute: string = this.isOwnerAdmin ? '/dashboard/summary' : '/dashboard/me';

  expandedGroups: Record<string, boolean> = {
    accesos: false,
    operaciones: true,
    divisas: false,
    seguridad: false,
    contabilidad: false,
    reportes: true
  };

  generalItems: MenuItem[] = [
    { label: 'Dashboard', route: this.dashboardRoute, icon: 'layout-dashboard' },
    { label: 'Notificaciones', route: '/dashboard/notifications', icon: 'bell' }
  ];

  customerMenuGroups: MenuGroup[] = [
      {
        id: 'mis-cuentas',
        label: 'Mi banca',
        icon: 'wallet',
        items: [
        {
          label: 'Mis cuentas',
            route: '/dashboard/me/accounts',
            icon: 'wallet',
            permissions: ['me.accounts.list', 'me.accounts.view', 'me.accounts.balance.read', 'me.accounts.update.alias', 'me.accounts.create']
          },
          {
            label: 'Mis movimientos',
            route: '/dashboard/me/transactions',
            icon: 'arrow-right-left',
            permissions: ['me.transactions.read', 'me.transactions.detail', 'me.transactions.transfer', 'me.transactions.deposit', 'me.transactions.withdrawal', 'me.transactions.payment', 'me.transactions.hold', 'me.transactions.release', 'me.transactions.qr.create', 'me.transactions.qr.read', 'me.transactions.qr.cancel', 'me.transactions.qr.confirm']
          }
        ]
      }
    ];

  tenantOwnerMenuGroups: MenuGroup[] = [
    {
      id: 'accesos',
      label: 'Accesos y Control',
      icon: 'shield',
      items: [
        {
          label: 'Usuarios',
          route: '/dashboard/users',
          icon: 'users',
          permissions: ['users.list', 'users.create', 'users.detail', 'users.update', 'users.activate', 'users.deactivate']
        },
        {
          label: 'Roles',
          route: '/dashboard/roles',
          icon: 'key',
          permissions: [
            'access.roles.read',
            'access.roles.create',
            'access.roles.detail',
            'access.roles.update',
            'access.roles.activate',
            'access.roles.deactivate',
            'access.users.roles.read',
            'access.users.roles.assign'
          ]
        },
        {
          label: 'Permisos',
          route: '/dashboard/permissions',
          icon: 'badge-check',
          permissions: ['access.permissions.read']
        }
      ]
    },
    {
      id: 'operaciones',
      label: 'Operaciones',
      icon: 'briefcase',
      items: [
        {
          label: 'Cuentas Bancarias',
          route: '/dashboard/accounts',
          icon: 'credit-card',
          permissions: [
            'accounts.create',
            'accounts.list',
            'accounts.view',
            'accounts.balance.read',
            'accounts.update',
            'accounts.approve',
            'accounts.activate',
            'accounts.block',
            'accounts.freeze',
            'accounts.close',
            'accounts.transactions.read'
          ]
        },
        {
          label: 'Transacciones',
          route: '/dashboard/transactions',
          icon: 'arrow-right-left',
          permissions: [
            'transactions.read',
            'transactions.detail',
            'transactions.create.transfer',
            'transactions.create.deposit',
            'transactions.create.withdrawal',
            'transactions.create.payment',
            'transactions.reverse',
            'transactions.refund',
            'transactions.fee',
            'transactions.hold',
            'transactions.release',
            'transactions.adjust',
            'transactions.admin.read',
            'transactions.admin.export',
            'transactions.qr.create',
            'transactions.qr.confirm'
          ]
        }
      ]
    },
    {
      id: 'divisas',
      label: 'Divisas y Tarifas',
      icon: 'dollar-sign',
      items: [
        {
          label: 'Tipos de Cambio',
          route: '/dashboard/fx/rates',
          icon: 'refresh-ccw',
          permissions: ['fx.rates.read', 'fx.rates.detail', 'fx.rates.create', 'fx.rates.update', 'fx.rates.delete']
        },
        {
          label: 'Comisiones',
          route: '/dashboard/fx/fees',
          icon: 'percent',
          permissions: ['fx.fees.read', 'fx.fees.detail', 'fx.fees.create', 'fx.fees.update', 'fx.fees.delete']
        }
      ]
    },
    {
      id: 'seguridad',
      label: 'Seguridad',
      icon: 'lock',
      items: [
        {
          label: 'Configuraciones',
          route: '/dashboard/settings',
          icon: 'settings'
        },
        {
          label: 'Límites Operativos',
          route: '/dashboard/limits/rules',
          icon: 'shield-alert',
          permissions: ['limits.read', 'limits.detail', 'limits.create', 'limits.update', 'limits.delete', 'limits.evaluate']
        }
      ]
    },
    {
      id: 'contabilidad',
      label: 'Contabilidad',
      icon: 'book-open',
      items: [
        {
          label: 'Períodos',
          route: '/dashboard/accounting/periods',
          icon: 'calendar',
          permissions: ['accounting.periods.read', 'accounting.periods.create', 'accounting.periods.close']
        },
        {
          label: 'Asientos',
          route: '/dashboard/accounting/journal-entries',
          icon: 'file-text',
          permissions: ['accounting.journal.read', 'accounting.journal.detail']
        }
      ]
    },
    {
      id: 'reportes',
      label: 'Reportes',
      icon: 'bar-chart-3',
      items: [
        {
          label: 'Explorador de reportes',
          route: '/dashboard/reports',
          icon: 'file-chart-column',
          permissions: ['reports.analytic.read', 'reports.managerial.read', 'reports.executions.read']
        },
        {
          label: 'Historial',
          route: '/dashboard/reports/history',
          icon: 'clock',
          permissions: ['reports.executions.read']
        }
      ]
    }
  ];

  platformMenuItems: MenuItem[] = [
    { label: 'Dashboard', route: '/platform/dashboard', icon: 'layout-dashboard' },
    { label: 'Mi perfil', route: '/platform/profile', icon: 'user-circle-2' },
    { label: 'Seguridad', route: '/platform/security', icon: 'lock' },
    { label: 'Configuraciones', route: '/platform/settings', icon: 'settings' },
    { label: 'Respaldos', route: '/platform/backups', icon: 'arrow-down-to-line' },
    { label: 'Planes', route: '/platform/plans', icon: 'credit-card' },
    { label: 'Tenants', route: '/platform/tenants', icon: 'building-2' },
    { label: 'Suscripciones', route: '/platform/subscriptions', icon: 'dollar-sign' },
    { label: 'Auditoría', route: '/platform/audit', icon: 'clipboard-list' }
  ];

  readonly menuGroups: MenuGroup[] = this.isOwnerAdmin
    ? this.tenantOwnerMenuGroups
    : [...this.customerMenuGroups, ...this.tenantOwnerMenuGroups]
        .map(group => ({
          ...group,
          items: group.items.filter(item => this.hasMenuItemAccess(item))
        }))
        .filter(group => group.items.length > 0);

  get isPlatformRoute(): boolean {
    return this.router.url.startsWith('/platform');
  }

  private hasMenuItemAccess(item: MenuItem): boolean {
    if (!item.permissions || item.permissions.length === 0) {
      return true;
    }

    return this.permissionService.hasAnyPermission(...item.permissions);
  }

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
