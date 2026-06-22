import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServiceBillResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-platform-service-bill-table',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="overflow-hidden rounded-[24px] border border-[#C8E6C9] bg-white shadow-sm">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-[#E8F2E2]">
          <thead class="bg-[#FAFCF8]">
            <tr>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Proveedor</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Código</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Titular</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Periodo</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Monto</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Estado</th>
              <th class="px-5 py-3 text-right text-xs font-bold uppercase tracking-wide text-[#567157]">Acciones</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-[#E8F2E2] bg-white">
            <tr *ngFor="let bill of bills" class="hover:bg-[#FAFCF8]">
              <td class="whitespace-nowrap px-5 py-4">
                <p class="text-sm font-bold text-[#1B5E20]">{{ providerName(bill) }}</p>
                <p class="text-xs text-[#6B7D6C]">{{ providerCode(bill) }}</p>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">{{ bill.serviceCustomerCode }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ bill.customerName }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ bill.billingPeriod }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">
                {{ bill.amount | number:'1.2-2' }} {{ bill.currency }}
              </td>
              <td class="whitespace-nowrap px-5 py-4">
                <span class="inline-flex rounded-full px-3 py-1 text-xs font-bold" [ngClass]="statusClass(bill.status)">
                  {{ statusLabel(bill.status) }}
                </span>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-right">
                <button
                  *ngIf="bill.status === 'PENDING'"
                  type="button"
                  (click)="cancel.emit(bill)"
                  class="cursor-pointer rounded-full border border-red-200 px-3 py-2 text-xs font-bold text-red-700 transition-colors hover:bg-red-50">
                  Cancelar
                </button>
              </td>
            </tr>

            <tr *ngIf="bills.length === 0">
              <td colspan="7" class="px-5 py-10 text-center">
                <p class="text-sm font-semibold text-[#1B5E20]">No hay deudas registradas</p>
                <p class="mt-1 text-sm text-[#6B7D6C]">Crea una deuda para que los clientes puedan pagar.</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class PlatformServiceBillTableComponent {
  @Input() bills: ServiceBillResponse[] = [];
  @Output() cancel = new EventEmitter<ServiceBillResponse>();

  providerName(bill: ServiceBillResponse): string {
    return bill.provider?.name || bill.providerName || '-';
  }

  providerCode(bill: ServiceBillResponse): string {
    return bill.provider?.code || bill.providerCode || '-';
  }

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'Pendiente',
      PAID: 'Pagada',
      EXPIRED: 'Vencida',
      CANCELLED: 'Cancelada',
      REVERSED: 'Revertida'
    };

    return labels[status] ?? status;
  }

  statusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING: 'bg-yellow-50 text-yellow-700',
      PAID: 'bg-[#E8F5E9] text-[#1B5E20]',
      EXPIRED: 'bg-orange-50 text-orange-700',
      CANCELLED: 'bg-slate-100 text-slate-600',
      REVERSED: 'bg-purple-50 text-purple-700'
    };

    return classes[status] ?? 'bg-slate-100 text-slate-600';
  }
}
