import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `
    <header class="flex items-center justify-between w-full h-full px-6 bg-[var(--background)]/80 backdrop-blur-md">
      
      <div></div>

      <div class="flex items-center gap-5">
        
        <button class="relative p-2 text-[var(--muted-foreground)] rounded-full hover:bg-[var(--accent)] hover:text-[var(--accent-foreground)] transition-colors focus-visible:outline-none">
          <span class="absolute top-1.5 right-1.5 w-2 h-2 bg-[var(--destructive)] rounded-full border-2 border-[var(--background)]"></span>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0" />
          </svg>
        </button>

        <div class="h-6 w-px bg-[var(--border)]"></div>
        
        <div class="flex items-center gap-3">
          <div class="flex flex-col items-end hidden sm:flex">
            <span class="text-sm font-semibold text-[var(--foreground)] leading-none">{{ userName() }}</span>
            <button (click)="logout.emit()" class="text-xs text-[var(--muted-foreground)] hover:text-[var(--foreground)] font-medium mt-1 transition-colors">
              Cerrar sesión
            </button>
          </div>
          <div class="flex items-center justify-center w-9 h-9 text-[var(--primary-foreground)] bg-[var(--primary)] rounded-full shadow-sm select-none ring-2 ring-[var(--background)] outline outline-1 outline-[var(--border)]">
            <span class="text-sm font-bold tracking-wider">{{ userInitials() }}</span>
          </div>
        </div>
        
      </div>
    </header>
  `
})
export class HeaderComponent {
  userName = input<string>('Superadmin');
  userInitials = input<string>('SA');
  logout = output<void>();
}