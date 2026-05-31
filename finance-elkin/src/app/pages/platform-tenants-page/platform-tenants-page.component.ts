// pages/platform-tenants-page/platform-tenants-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus, X } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformTenantListUseCase } from '../../features/platform/application/platform-tenant-list.usecase';
import { PlatformTenantCreateUseCase } from '../../features/platform/application/platform-tenant-create.usecase';
import { PlatformTenantActivateUseCase } from '../../features/platform/application/platform-tenant-activate.usecase';
import { PlatformTenantDeactivateUseCase } from '../../features/platform/application/platform-tenant-deactivate.usecase';
import { PlatformTenantTableComponent } from '../../features/platform/ui/platform-tenant-table/platform-tenant-table.component';
import { PlatformTenantFormComponent } from '../../features/platform/ui/platform-tenant-form/platform-tenant-form.component';

@Component({
  selector: 'app-platform-tenants-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformTenantTableComponent, 
    PlatformTenantFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Tenants</h1>
        <button (click)="showCreateForm = !showCreateForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Nuevo Tenant
        </button>
      </div>

      @if (showCreateForm) {
        <div class="mb-6">
          <app-platform-tenant-form
            [isLoading]="createUseCase.status() === 'loading'"
            [error]="createUseCase.error()"
            (submitForm)="onCreateTenant($event)"
            (cancel)="showCreateForm = false">
          </app-platform-tenant-form>
        </div>
      }

      <!-- Tabla con indicador de carga -->
      <app-platform-tenant-table
        [tenants]="listUseCase.tenants()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)"
        (activate)="onActivate($event)"
        (deactivate)="onDeactivate($event)">
      </app-platform-tenant-table>

      <!-- Modal de detalles -->
      @if (selectedTenant) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle del Tenant</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedTenant.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Nombre:</span> {{ selectedTenant.name }}</div>
              <div><span class="font-medium text-[#2E7D32]">Slug:</span> {{ selectedTenant.slug }}</div>
              <div><span class="font-medium text-[#2E7D32]">Schema:</span> {{ selectedTenant.schemaName }}</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedTenant.active ? 'text-[#2E7D32]' : 'text-[#C62828]'">{{ selectedTenant.status }}</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Creado:</span> {{ selectedTenant.createdAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Actualizado:</span> {{ selectedTenant.updatedAt | date:'medium' }}</div>
            </div>
            <div class="flex justify-end mt-6">
              <button (click)="closeModal()" class="px-4 py-2 bg-[#2E7D32] text-white rounded-lg hover:bg-[#4CAF50]">Cerrar</button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class PlatformTenantsPageComponent implements OnInit {
  protected listUseCase = inject(PlatformTenantListUseCase);
  protected createUseCase = inject(PlatformTenantCreateUseCase);
  protected activateUseCase = inject(PlatformTenantActivateUseCase);
  protected deactivateUseCase = inject(PlatformTenantDeactivateUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showCreateForm = false;
  selectedTenant: any = null;

  ngOnInit(): void {
    this.loadTenants();
  }

  async loadTenants(): Promise<void> {
    await this.listUseCase.loadTenants();
    this.cdr.detectChanges();
  }

  async onCreateTenant(data: { name: string; slug: string; planCode: string }): Promise<void> {
    const success = await this.createUseCase.createTenant(data);
    if (success) {
      this.toastService.success('Tenant creado exitosamente');
      this.showCreateForm = false;
      this.createUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.createUseCase.error() || 'Error al crear tenant');
    }
  }

  // ✅ Activar tenant
  async onActivate(id: string): Promise<void> {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (!tenant) return;

    const confirmActivate = confirm(`¿Estás seguro de que deseas activar el tenant "${tenant.name}"?`);
    if (!confirmActivate) return;

    const success = await this.activateUseCase.activateTenant(id);
    if (success) {
      this.toastService.success(`Tenant "${tenant.name}" activado exitosamente`);
      this.activateUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.activateUseCase.error() || 'Error al activar tenant');
    }
  }

  // ✅ Desactivar tenant
  async onDeactivate(id: string): Promise<void> {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (!tenant) return;

    const confirmDeactivate = confirm(`¿Estás seguro de que deseas desactivar el tenant "${tenant.name}"?\n\nLos usuarios no podrán acceder hasta que sea reactivado.`);
    if (!confirmDeactivate) return;

    const success = await this.deactivateUseCase.deactivateTenant(id);
    if (success) {
      this.toastService.success(`Tenant "${tenant.name}" desactivado exitosamente`);
      this.deactivateUseCase.resetState();
      await this.loadTenants();
    } else {
      this.toastService.error(this.deactivateUseCase.error() || 'Error al desactivar tenant');
    }
  }

  viewDetails(id: string): void {
    const tenant = this.listUseCase.tenants().find(t => t.id === id);
    if (tenant) {
      this.selectedTenant = tenant;
      this.cdr.detectChanges();
    }
  }

  closeModal(): void {
    this.selectedTenant = null;
    this.cdr.detectChanges();
  }
}