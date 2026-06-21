import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-service-payments-loading-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-8 text-center shadow-sm">
      <div class="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-[#F1F8E9] text-[#2E7D32]">
        <lucide-icon name="loader-circle" class="h-6 w-6 animate-spin"></lucide-icon>
      </div>

      <p class="mt-4 text-sm font-semibold text-[#1B5E20]">{{ message }}</p>
    </div>
  `
})
export class ServicePaymentsLoadingCardComponent {
  @Input() message = 'Cargando...';
}
