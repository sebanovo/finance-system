import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-service-payments-confirm-dialog',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-black/30 backdrop-blur-sm" (click)="cancel.emit()"></div>

      <div class="relative w-full max-w-lg rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-2xl">
        <div class="flex items-start gap-4">
          <div class="rounded-2xl bg-red-50 p-3 text-red-700">
            <lucide-icon name="triangle-alert" class="h-6 w-6"></lucide-icon>
          </div>

          <div class="flex-1">
            <h3 class="text-xl font-black tracking-tight text-[#1B5E20]">{{ title }}</h3>
            <p class="mt-2 text-sm text-[#6B7D6C]">{{ message }}</p>
          </div>
        </div>

        <div class="mt-6 flex justify-end gap-3">
          <button
            type="button"
            (click)="cancel.emit()"
            class="rounded-full border border-[#DDEED8] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            Cancelar
          </button>

          <button
            type="button"
            (click)="confirm.emit()"
            class="rounded-full bg-red-700 px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-red-800">
            {{ confirmLabel }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class ServicePaymentsConfirmDialogComponent {
  @Input() isOpen = false;
  @Input() title = 'Confirmar acción';
  @Input() message = '¿Deseas continuar?';
  @Input() confirmLabel = 'Confirmar';

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
