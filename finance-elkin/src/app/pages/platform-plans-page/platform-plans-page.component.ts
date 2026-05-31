// pages/platform-plans-page/platform-plans-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformPlanListUseCase } from '../../features/platform/application/platform-plan-list.usecase';
import { PlatformPlanCreateUseCase } from '../../features/platform/application/platform-plan-create.usecase';
import { PlatformPlanActivateUseCase } from '../../features/platform/application/platform-plan-activate.usecase';
import { PlatformPlanDeactivateUseCase } from '../../features/platform/application/platform-plan-deactivate.usecase';
import { PlatformPlanTableComponent } from '../../features/platform/ui/platform-plan-table/platform-plan-table.component';
import { PlatformPlanFormComponent } from '../../features/platform/ui/platform-plan-form/platform-plan-form.component';

@Component({
  selector: 'app-platform-plans-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformPlanTableComponent, 
    PlatformPlanFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Planes</h1>
        <button (click)="showCreateForm = !showCreateForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Nuevo Plan
        </button>
      </div>

      @if (showCreateForm) {
        <div class="mb-6">
          <app-platform-plan-form
            [isLoading]="createUseCase.status() === 'loading'"
            [error]="createUseCase.error()"
            (submitForm)="onCreatePlan($event)"
            (cancel)="showCreateForm = false">
          </app-platform-plan-form>
        </div>
      }

      <app-platform-plan-table
        [plans]="listUseCase.plans()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)"
        (activate)="onActivate($event)"
        (deactivate)="onDeactivate($event)">
      </app-platform-plan-table>

      <!-- Modal de detalles -->
      @if (selectedPlan) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle del Plan</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedPlan.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Código:</span> {{ selectedPlan.code }}</div>
              <div><span class="font-medium text-[#2E7D32]">Nombre:</span> {{ selectedPlan.name }}</div>
              <div><span class="font-medium text-[#2E7D32]">Descripción:</span> {{ selectedPlan.description }}</div>
              <div><span class="font-medium text-[#2E7D32]">Tipo:</span> {{ selectedPlan.planType }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. usuarios:</span> {{ selectedPlan.maxUsers }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. roles:</span> {{ selectedPlan.maxRoles }}</div>
              <div><span class="font-medium text-[#2E7D32]">Días de prueba:</span> {{ selectedPlan.trialDays || 'N/A' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedPlan.active ? 'text-[#2E7D32]' : 'text-[#C62828]'">{{ selectedPlan.active ? 'ACTIVO' : 'INACTIVO' }}</span>
              </div>
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
export class PlatformPlansPageComponent implements OnInit {
  protected listUseCase = inject(PlatformPlanListUseCase);
  protected createUseCase = inject(PlatformPlanCreateUseCase);
  protected activateUseCase = inject(PlatformPlanActivateUseCase);
  protected deactivateUseCase = inject(PlatformPlanDeactivateUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showCreateForm = false;
  selectedPlan: any = null;

  ngOnInit(): void {
    this.loadPlans();
  }

  async loadPlans(): Promise<void> {
    await this.listUseCase.loadPlans();
    this.cdr.detectChanges();
  }

  async onCreatePlan(data: any): Promise<void> {
    const success = await this.createUseCase.createPlan(data);
    if (success) {
      this.toastService.success('Plan creado exitosamente');
      this.showCreateForm = false;
      this.createUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.createUseCase.error() || 'Error al crear plan');
    }
  }

  async onActivate(id: string): Promise<void> {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (!plan) return;

    const confirmActivate = confirm(`¿Estás seguro de que deseas activar el plan "${plan.name}"?`);
    if (!confirmActivate) return;

    const success = await this.activateUseCase.activatePlan(id);
    if (success) {
      this.toastService.success(`Plan "${plan.name}" activado exitosamente`);
      this.activateUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.activateUseCase.error() || 'Error al activar plan');
    }
  }

  async onDeactivate(id: string): Promise<void> {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (!plan) return;

    const confirmDeactivate = confirm(`¿Estás seguro de que deseas desactivar el plan "${plan.name}"?`);
    if (!confirmDeactivate) return;

    const success = await this.deactivateUseCase.deactivatePlan(id);
    if (success) {
      this.toastService.success(`Plan "${plan.name}" desactivado exitosamente`);
      this.deactivateUseCase.resetState();
      await this.loadPlans();
    } else {
      this.toastService.error(this.deactivateUseCase.error() || 'Error al desactivar plan');
    }
  }

  viewDetails(id: string): void {
    const plan = this.listUseCase.plans().find(p => p.id === id);
    if (plan) {
      this.selectedPlan = plan;
      this.cdr.detectChanges();
    }
  }

  closeModal(): void {
    this.selectedPlan = null;
    this.cdr.detectChanges();
  }
}