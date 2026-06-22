import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-service-payments-state-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-8 text-center shadow-sm">
      <div class="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-[#F1F8E9] text-[#2E7D32]">
        <lucide-icon [name]="icon" class="h-6 w-6"></lucide-icon>
      </div>

      <h3 class="mt-4 text-lg font-black tracking-tight text-[#1B5E20]">{{ title }}</h3>
      <p class="mx-auto mt-2 max-w-xl text-sm text-[#6B7D6C]">{{ message }}</p>

      <button
        *ngIf="actionLabel"
        type="button"
        (click)="action.emit()"
        class="mt-5 inline-flex cursor-pointer items-center justify-center rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
        {{ actionLabel }}
      </button>
    </div>
  `
})
export class ServicePaymentsStateCardComponent {
  @Input() icon = 'info';
  @Input() title = 'Sin resultados';
  @Input() message = 'No hay información para mostrar.';
  @Input() actionLabel: string | null = null;

  @Output() action = new EventEmitter<void>();
}
