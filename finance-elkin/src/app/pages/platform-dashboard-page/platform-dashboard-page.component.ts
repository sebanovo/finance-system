import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { PlatformStorageService } from '../../features/platform/lib/platform-storage.service';

@Component({
  selector: 'app-platform-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="p-6">
      <h1 class="text-2xl font-bold text-[#2E7D32] mb-6">Dashboard de la Plataforma</h1>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Total Tenants</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Planes Activos</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
        <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
          <h3 class="text-lg font-semibold text-[#2E7D32]">Suscripciones</h3>
          <p class="text-3xl font-bold mt-2">0</p>
        </div>
      </div>
    </div>
  `
})
export class PlatformDashboardPageComponent {
  private router = inject(Router);
  private platformStorage = inject(PlatformStorageService);

  onLogout(): void {
    this.platformStorage.clear();
    this.router.navigate(['/platform/login']);
  }
}