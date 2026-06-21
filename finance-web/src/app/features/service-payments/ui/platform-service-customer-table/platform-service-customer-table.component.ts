import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServiceCustomerResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-platform-service-customer-table',
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
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Estado</th>
              <th class="px-5 py-3 text-right text-xs font-bold uppercase tracking-wide text-[#567157]">Acciones</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-[#E8F2E2] bg-white">
            <tr *ngFor="let customer of customers" class="hover:bg-[#FAFCF8]">
              <td class="whitespace-nowrap px-5 py-4">
                <p class="text-sm font-bold text-[#1B5E20]">{{ customer.providerName }}</p>
                <p class="text-xs text-[#6B7D6C]">{{ customer.providerCode }}</p>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">{{ customer.serviceCustomerCode }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ customer.customerName }}</td>
              <td class="whitespace-nowrap px-5 py-4">
                <span
                  class="inline-flex rounded-full px-3 py-1 text-xs font-bold"
                  [ngClass]="customer.status === 'ACTIVE' ? 'bg-[#E8F5E9] text-[#1B5E20]' : 'bg-slate-100 text-slate-600'">
                  {{ customer.status === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-right">
                <button
                  type="button"
                  (click)="edit.emit(customer)"
                  class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                  <lucide-icon name="pencil" [size]="16"></lucide-icon>
                </button>
              </td>
            </tr>

            <tr *ngIf="customers.length === 0">
              <td colspan="5" class="px-5 py-10 text-center">
                <p class="text-sm font-semibold text-[#1B5E20]">No hay clientes de servicio registrados</p>
                <p class="mt-1 text-sm text-[#6B7D6C]">Crea códigos válidos para generar deudas.</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class PlatformServiceCustomerTableComponent {
  @Input() customers: ServiceCustomerResponse[] = [];
  @Output() edit = new EventEmitter<ServiceCustomerResponse>();
}
