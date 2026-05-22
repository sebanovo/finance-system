import { Component, inject, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
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
    <!-- Mobile Hamburger Button (Floating top-left when sidebar is closed) -->
    <button 
      *ngIf="!isMobileOpen"
      (click)="toggleMobileMenu()"
      class="md:hidden fixed top-3 left-4 z-40 p-2 bg-card border border-border rounded-md shadow-sm text-foreground hover:bg-accent transition-colors"
    >
      <lucide-icon name="menu" class="h-5 w-5"></lucide-icon>
    </button>

    <!-- Mobile Overlay Backdrop -->
    <div 
      *ngIf="isMobileOpen" 
      @fadeOverlay
      (click)="closeMobileMenu()"
      class="md:hidden fixed inset-0 bg-background/80 backdrop-blur-sm z-40"
    ></div>

    <!-- Sidebar Container -->
    <!-- Usamos Tailwind para el slide responsive (md:translate-x-0) ya que es la forma más limpia y robusta para media queries -->
    <aside 
      class="fixed inset-y-0 left-0 z-50 w-64 h-full flex flex-col bg-sidebar border-r border-sidebar-border text-sidebar-foreground shadow-xl md:shadow-none transition-transform duration-300 ease-in-out md:relative md:translate-x-0"
      [class.-translate-x-full]="!isMobileOpen"
      [class.translate-x-0]="isMobileOpen"
    >
      <!-- Logo / Brand Area -->
      <div class="h-16 flex items-center justify-between px-6 border-b border-sidebar-border/50 shrink-0">
        <div class="flex items-center gap-3">
          <div class="h-8 w-8 rounded-md bg-sidebar-primary flex items-center justify-center text-sidebar-primary-foreground font-bold shadow-sm">
            P
          </div>
          <span class="font-bold text-lg tracking-tight text-sidebar-foreground">PROSPERA</span>
        </div>
        
        <!-- Mobile Close Button -->
        <button (click)="closeMobileMenu()" class="md:hidden p-1.5 text-muted-foreground hover:text-foreground rounded-md hover:bg-accent transition-colors">
          <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
        </button>
      </div>

      <!-- Navigation -->
      <nav class="flex-1 overflow-y-auto py-4 px-3 space-y-1 custom-scrollbar">
        
        <!-- Elementos Generales (Sin Acordeón) -->
        <a *ngFor="let item of generalItems" 
           [routerLink]="item.route" 
           routerLinkActive="bg-sidebar-primary text-sidebar-primary-foreground font-semibold shadow-sm" 
           [routerLinkActiveOptions]="{exact: item.route === '/dashboard'}"
           (click)="closeMobileMenu()"
           class="flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground transition-all duration-200 group">
          <lucide-icon [name]="item.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity text-white"></lucide-icon>
          {{ item.label }}
        </a>

        <div class="h-4"></div> <!-- Espaciador -->

        <!-- Grupos Acordeón -->
        <div *ngFor="let group of menuGroups" class="mb-2">
          
          <!-- Header del Grupo -->
          <button 
            (click)="toggleGroup(group.id)"
            class="w-full flex items-center justify-between px-3 py-2 text-sm font-medium text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground rounded-md transition-colors group"
          >
            <div class="flex items-center gap-3">
              <lucide-icon [name]="group.icon" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity text-white"></lucide-icon>
              <span>{{ group.label }}</span>
            </div>
            <lucide-icon 
              name="chevron-down" 
              class="h-4 w-4 opacity-50 text-white"
              [@rotateIcon]="expandedGroups[group.id] ? 'open' : 'closed'"
            ></lucide-icon>
          </button>

          <!-- Contenido del Grupo (Animado con Angular Animations) -->
          <div [@accordion]="expandedGroups[group.id] ? 'open' : 'closed'" class="overflow-hidden px-2">
            <div class="flex flex-col space-y-1 pl-4 border-l border-sidebar-border/50 ml-2 py-1">
              <a *ngFor="let item of group.items" 
                 [routerLink]="item.route" 
                 routerLinkActive="text-primary font-semibold bg-primary/5"
                 (click)="closeMobileMenu()"
                 class="flex items-center gap-3 px-3 py-2 rounded-md text-sm text-sidebar-foreground/70 hover:text-sidebar-foreground hover:bg-sidebar-accent transition-all duration-200">
                <lucide-icon [name]="item.icon" class="h-3.5 w-3.5 opacity-60 text-white"></lucide-icon>
                {{ item.label }}
              </a>
            </div>
          </div>
          
        </div>
      </nav>

      <!-- Sidebar Footer / Logout -->
      <div class="p-4 border-t border-sidebar-border/50 shrink-0">
        <button 
          (click)="onLogout()" 
          class="flex w-full items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium text-sidebar-foreground/80 hover:bg-destructive hover:text-destructive-foreground transition-all duration-200 group"
        >
          <lucide-icon name="log-out" class="h-4 w-4 opacity-70 group-hover:opacity-100 transition-opacity text-white"></lucide-icon>
          Cerrar Sesión
        </button>
      </div>
    </aside>
  `,
  styles: [`
    /* Scrollbar minimalista para el sidebar */
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
    :host-context(.dark) .custom-scrollbar::-webkit-scrollbar-thumb {
      background: rgba(255,255,255,0.1);
    }
  `]
})
export class SidebarComponent {
  @Output() logoutAction = new EventEmitter<void>();

  isMobileOpen = false;

  // Estado de los acordeones (por defecto Operaciones abierto)
  expandedGroups: Record<string, boolean> = {
    accesos: false,
    operaciones: true,
    divisas: false,
    seguridad: false,
    contabilidad: false
  };

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
