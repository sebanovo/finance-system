import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServicePaymentResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-my-service-payment-history-table',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="overflow-hidden rounded-[24px] border border-[#C8E6C9] bg-white shadow-sm">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-[#E8F2E2]">
          <thead class="bg-[#FAFCF8]">
            <tr>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Recibo</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Proveedor</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Servicio</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Periodo</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Monto</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Cuenta</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Fecha</th>
              <th class="px-5 py-3 text-right text-xs font-bold uppercase tracking-wide text-[#567157]">Acciones</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-[#E8F2E2] bg-white">
            <tr *ngFor="let payment of payments" class="hover:bg-[#FAFCF8]">
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">{{ payment.receiptNumber }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ payment.provider.name }}</td>
              <td class="whitespace-nowrap px-5 py-4">
                <p class="text-sm font-semibold text-[#1B5E20]">{{ payment.serviceCustomerName }}</p>
                <p class="text-xs text-[#6B7D6C]">{{ payment.serviceCustomerCode }}</p>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ payment.billingPeriod }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">
                {{ payment.amount | number:'1.2-2' }} {{ payment.currency }}
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ payment.sourceAccountNumber }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ payment.paidAt | date:'short' }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-right">
                <button
                  type="button"
                  (click)="detail.emit(payment)"
                  class="cursor-pointer rounded-full border border-[#DDEED8] px-3 py-2 text-xs font-bold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                  Ver comprobante
                </button>
              </td>
            </tr>

            <tr *ngIf="payments.length === 0">
              <td colspan="8" class="px-5 py-10 text-center">
                <p class="text-sm font-semibold text-[#1B5E20]">Aún no tienes pagos registrados</p>
                <p class="mt-1 text-sm text-[#6B7D6C]">Cuando pagues luz, agua, internet o cable aparecerán aquí.</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class MyServicePaymentHistoryTableComponent {
  @Input() payments: ServicePaymentResponse[] = [];
  @Output() detail = new EventEmitter<ServicePaymentResponse>();
}
