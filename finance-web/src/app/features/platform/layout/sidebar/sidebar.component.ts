import { Component, input, output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="flex flex-col h-full overflow-hidden bg-[var(--sidebar)] text-[var(--sidebar-foreground)]">
      
      <div class="flex items-center h-16 px-4 border-b border-[var(--sidebar-border)] shrink-0" 
           [class.justify-between]="isOpen()" 
           [class.justify-center]="!isOpen()">
        
        @if (isOpen()) {
          <div class="flex items-center gap-2 overflow-hidden">
            <div class="w-8 h-8 bg-[var(--sidebar-primary)] rounded-[calc(var(--radius)-4px)] flex items-center justify-center shrink-0">
              <span class="text-[var(--sidebar-primary-foreground)] font-bold text-sm">F</span>
            </div>
            <span class="text-lg font-bold truncate">Financify</span>
          </div>
        }
        
        <button (click)="onToggle.emit()" 
                class="p-1.5 text-[var(--muted-foreground)] rounded-[calc(var(--radius)-4px)] hover:bg-[var(--sidebar-accent)] hover:text-[var(--sidebar-accent-foreground)] transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[var(--sidebar-ring)] shrink-0">
          @if (isOpen()) {
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
            </svg>
          } @else {
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
            </svg>
          }
        </button>
      </div>

      <nav class="flex-1 py-4 overflow-y-auto">
        <ul class="flex flex-col gap-1.5 px-3">
          
          <li>
            <a routerLink="/platform/tenants" 
               routerLinkActive="bg-[var(--sidebar-accent)] text-[var(--sidebar-accent-foreground)] font-semibold" 
               [routerLinkActiveOptions]="{exact: false}"
               #rlaTenants="routerLinkActive"
               class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-[calc(var(--radius)-4px)] transition-all duration-200 group"
               [class.text-[var(--muted-foreground)]]="!rlaTenants.isActive"
               [class.hover:bg-[var(--sidebar-accent)]]="!rlaTenants.isActive"
               [class.hover:text-[var(--sidebar-accent-foreground)]]="!rlaTenants.isActive"
               [class.justify-center]="!isOpen()">
              
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" 
                   class="w-5 h-5 shrink-0 transition-colors duration-200"
                   [class.text-[var(--sidebar-accent-foreground)]]="rlaTenants.isActive"
                   [class.group-hover:text-[var(--sidebar-accent-foreground)]]="!rlaTenants.isActive">
                <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 21h19.5m-18-18v18m10.5-18v18m6-13.5V21M6.75 6.75h.75m-.75 3h.75m-.75 3h.75m3-6h.75m-.75 3h.75m-.75 3h.75M6.75 21v-3.375c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21M3 3h12m-.75 4.5H21m-3.75 3.75h.008v.008h-.008v-.008zm0 3h.008v.008h-.008v-.008zm0 3h.008v.008h-.008v-.008z" />
              </svg>
              
              @if (isOpen()) {
                <span class="truncate">Empresas</span>
              }
            </a>
          </li>

          <li>
            <a routerLink="/platform/admins" 
               routerLinkActive="bg-[var(--sidebar-accent)] text-[var(--sidebar-accent-foreground)] font-semibold"
               #rlaAdmins="routerLinkActive"
               class="flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-[calc(var(--radius)-4px)] transition-all duration-200 group"
               [class.text-[var(--muted-foreground)]]="!rlaAdmins.isActive"
               [class.hover:bg-[var(--sidebar-accent)]]="!rlaAdmins.isActive"
               [class.hover:text-[var(--sidebar-accent-foreground)]]="!rlaAdmins.isActive"
               [class.justify-center]="!isOpen()">
              
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" 
                   class="w-5 h-5 shrink-0 transition-colors duration-200"
                   [class.text-[var(--sidebar-accent-foreground)]]="rlaAdmins.isActive"
                   [class.group-hover:text-[var(--sidebar-accent-foreground)]]="!rlaAdmins.isActive">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
              </svg>

              @if (isOpen()) {
                <span class="truncate">Administradores</span>
              }
            </a>
          </li>

        </ul>
      </nav>
    </aside>
  `
})
export class SidebarComponent {
  isOpen = input<boolean>(true);
  onToggle = output<void>();
}