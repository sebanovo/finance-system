import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `
    <header class="h-16 flex items-center justify-between px-8 bg-card border-b border-border shadow-sm w-full">
      <h1 class="text-xl font-semibold text-foreground">Panel de Control</h1>
      <!-- User Profile / Notifications placeholder -->
      <div class="flex items-center gap-4">
          <div class="h-8 w-8 rounded-full bg-secondary text-secondary-foreground flex items-center justify-center font-medium text-sm">
            US
          </div>
      </div>
    </header>
  `
})
export class HeaderComponent {}
