import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServiceProviderResponse } from '../../../../entities/service-payments';

@Component({
  selector: 'app-platform-service-provider-table',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div class="overflow-hidden rounded-[24px] border border-[#C8E6C9] bg-white shadow-sm">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-[#E8F2E2]">
          <thead class="bg-[#FAFCF8]">
            <tr>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Código</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Nombre</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Categoría</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Etiqueta</th>
              <th class="px-5 py-3 text-left text-xs font-bold uppercase tracking-wide text-[#567157]">Estado</th>
              <th class="px-5 py-3 text-right text-xs font-bold uppercase tracking-wide text-[#567157]">Acciones</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-[#E8F2E2] bg-white">
            <tr *ngFor="let provider of providers" class="hover:bg-[#FAFCF8]">
              <td class="whitespace-nowrap px-5 py-4 text-sm font-bold text-[#1B5E20]">{{ provider.code }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#1B5E20]">{{ provider.name }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ categoryLabel(provider.category) }}</td>
              <td class="whitespace-nowrap px-5 py-4 text-sm text-[#6B7D6C]">{{ provider.serviceCustomerCodeLabel || 'Código de cliente' }}</td>
              <td class="whitespace-nowrap px-5 py-4">
                <span
                  class="inline-flex rounded-full px-3 py-1 text-xs font-bold"
                  [ngClass]="provider.status === 'ACTIVE' ? 'bg-[#E8F5E9] text-[#1B5E20]' : 'bg-slate-100 text-slate-600'">
                  {{ provider.status === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
              <td class="whitespace-nowrap px-5 py-4 text-right">
                <div class="inline-flex items-center gap-2">
                  <button
                    type="button"
                    (click)="edit.emit(provider)"
                    class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                    <lucide-icon name="pencil" [size]="16"></lucide-icon>
                  </button>

                  <button
                    type="button"
                    (click)="toggleStatus.emit(provider)"
                    class="cursor-pointer rounded-full border border-[#DDEED8] px-3 py-2 text-xs font-bold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                    {{ provider.status === 'ACTIVE' ? 'Desactivar' : 'Activar' }}
                  </button>
                </div>
              </td>
            </tr>

            <tr *ngIf="providers.length === 0">
              <td colspan="6" class="px-5 py-10 text-center">
                <p class="text-sm font-semibold text-[#1B5E20]">No hay proveedores registrados</p>
                <p class="mt-1 text-sm text-[#6B7D6C]">Crea el primer proveedor para empezar.</p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class PlatformServiceProviderTableComponent {
  @Input() providers: ServiceProviderResponse[] = [];

  @Output() edit = new EventEmitter<ServiceProviderResponse>();
  @Output() toggleStatus = new EventEmitter<ServiceProviderResponse>();

  categoryLabel(category: string): string {
    const labels: Record<string, string> = {
      ELECTRICITY: 'Luz',
      WATER: 'Agua',
      INTERNET: 'Internet',
      TV_CABLE: 'Cable TV'
    };

    return labels[category] ?? category;
  }
}
