import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-service-payments-error-card',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="rounded-[24px] border border-red-200 bg-red-50 p-6 text-center">
      <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-white text-red-700">
        <lucide-icon name="circle-alert" class="h-6 w-6"></lucide-icon>
      </div>

      <p class="mt-4 text-sm font-bold text-red-800">{{ title }}</p>
      <p class="mt-1 text-sm text-red-700">{{ message }}</p>

      <button
        *ngIf="showRetry"
        type="button"
        (click)="retry.emit()"
        class="mt-5 inline-flex cursor-pointer items-center justify-center rounded-full bg-red-700 px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-red-800">
        Reintentar
      </button>
    </div>
  `
})
export class ServicePaymentsErrorCardComponent {
  @Input() title = 'Ocurrió un problema';
  @Input() message = 'No se pudo completar la operación.';
  @Input() showRetry = true;

  @Output() retry = new EventEmitter<void>();
}
