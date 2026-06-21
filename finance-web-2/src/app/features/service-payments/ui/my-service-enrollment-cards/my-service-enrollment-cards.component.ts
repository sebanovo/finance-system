import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServiceEnrollmentResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-my-service-enrollment-cards',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div *ngIf="enrollments.length > 0; else emptyState" class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      <article
        *ngFor="let enrollment of enrollments"
        class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm transition hover:shadow-md">
        <div class="flex items-start justify-between gap-4">
          <div class="flex items-start gap-3">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon [name]="iconName(enrollment.provider.category)" class="h-5 w-5"></lucide-icon>
            </div>

            <div>
              <h3 class="text-lg font-black text-[#1B5E20]">
                {{ enrollment.alias || enrollment.provider.name }}
              </h3>
              <p class="mt-1 text-sm font-semibold text-[#567157]">{{ enrollment.provider.name }}</p>
              <p class="mt-1 text-xs text-[#6B7D6C]">{{ categoryLabel(enrollment.provider.category) }}</p>
            </div>
          </div>

          <span
            class="rounded-full px-3 py-1 text-xs font-bold"
            [ngClass]="enrollment.status === 'ACTIVE' ? 'bg-[#E8F5E9] text-[#1B5E20]' : 'bg-slate-100 text-slate-600'">
            {{ enrollment.status === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
          </span>
        </div>

        <div class="mt-5 space-y-2 rounded-2xl bg-[#FAFCF8] p-4 text-sm">
          <div class="flex justify-between gap-4">
            <span class="text-[#6B7D6C]">Código</span>
            <span class="font-bold text-[#1B5E20]">{{ enrollment.serviceCustomerCode }}</span>
          </div>

          <div class="flex justify-between gap-4">
            <span class="text-[#6B7D6C]">Titular</span>
            <span class="text-right font-semibold text-[#1B5E20]">{{ enrollment.serviceCustomerName || '-' }}</span>
          </div>
        </div>

        <div class="mt-5 flex flex-wrap gap-2">
          <button
            type="button"
            (click)="viewDebts.emit(enrollment)"
            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#DDEED8] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="search" [size]="16"></lucide-icon>
            Consultar
          </button>

          <button
            type="button"
            (click)="pay.emit(enrollment)"
            class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
            <lucide-icon name="dollar-sign" [size]="16"></lucide-icon>
            Pagar
          </button>

          <button
            type="button"
            (click)="delete.emit(enrollment)"
            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-red-200 bg-white px-4 py-2 text-sm font-semibold text-red-700 transition-colors hover:bg-red-50">
            <lucide-icon name="trash-2" [size]="16"></lucide-icon>
            Eliminar
          </button>
        </div>
      </article>
    </div>

    <ng-template #emptyState>
      <div class="rounded-[24px] border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-8 text-center">
        <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-[#E8F5E9] text-[#2E7D32]">
          <lucide-icon name="clipboard-list" class="h-6 w-6"></lucide-icon>
        </div>
        <p class="mt-4 text-sm font-semibold text-[#1B5E20]">Aún no tienes servicios afiliados</p>
        <p class="mt-1 text-sm text-[#6B7D6C]">
          Afília luz, agua, internet o cable para consultar y pagar más rápido.
        </p>
      </div>
    </ng-template>
  `
})
export class MyServiceEnrollmentCardsComponent {
  @Input() enrollments: ServiceEnrollmentResponse[] = [];

  @Output() viewDebts = new EventEmitter<ServiceEnrollmentResponse>();
  @Output() pay = new EventEmitter<ServiceEnrollmentResponse>();
  @Output() delete = new EventEmitter<ServiceEnrollmentResponse>();

  categoryLabel(category: string): string {
    const labels: Record<string, string> = {
      ELECTRICITY: 'Luz',
      WATER: 'Agua',
      INTERNET: 'Internet',
      TV_CABLE: 'Cable TV'
    };

    return labels[category] ?? category;
  }

  iconName(category: string): string {
    const icons: Record<string, string> = {
      ELECTRICITY: 'sparkles',
      WATER: 'droplets',
      INTERNET: 'wifi',
      TV_CABLE: 'tv'
    };

    return icons[category] ?? 'clipboard-list';
  }
}
