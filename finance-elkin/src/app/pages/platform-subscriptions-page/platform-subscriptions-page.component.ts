// pages/platform-subscriptions-page/platform-subscriptions-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule, Plus, X } from 'lucide-angular';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PlatformSubscriptionListUseCase } from '../../features/platform/application/platform-subscription-list.usecase';
import { PlatformSubscriptionAssignUseCase } from '../../features/platform/application/platform-subscription-assign.usecase';
import { PlatformSubscriptionGetByIdUseCase } from '../../features/platform/application/platform-subscription-get-by-id.usecase';
import { PlatformSubscriptionTableComponent } from '../../features/platform/ui/platform-subscription-table/platform-subscription-table.component';
import { PlatformSubscriptionAssignFormComponent } from '../../features/platform/ui/platform-subscription-assign-form/platform-subscription-assign-form.component';

@Component({
  selector: 'app-platform-subscriptions-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    LucideAngularModule,
    PlatformSubscriptionTableComponent, 
    PlatformSubscriptionAssignFormComponent
  ],
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Gestión de Suscripciones</h1>
        <button (click)="showAssignForm = !showAssignForm" 
                class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg hover:bg-[#4CAF50] transition-colors flex items-center gap-2">
          <lucide-icon name="plus" class="h-5 w-5"></lucide-icon>
          Asignar Suscripción
        </button>
      </div>

      @if (showAssignForm) {
        <div class="mb-6">
          <app-platform-subscription-assign-form
            [isLoading]="assignUseCase.status() === 'loading'"
            [error]="assignUseCase.error()"
            (submitForm)="onAssignSubscription($event)"
            (cancel)="showAssignForm = false">
          </app-platform-subscription-assign-form>
        </div>
      }

      <app-platform-subscription-table
        [subscriptions]="listUseCase.subscriptions()"
        [isLoading]="listUseCase.status() === 'loading'"
        (viewDetails)="viewDetails($event)">
      </app-platform-subscription-table>

      <!-- Modal de detalles -->
      @if (selectedSubscription) {
        <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" (click)="closeModal()">
          <div class="bg-white rounded-xl max-w-lg w-full mx-4 p-6" (click)="$event.stopPropagation()">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-bold text-[#2E7D32]">Detalle de Suscripción</h2>
              <button (click)="closeModal()" class="text-[#666666] hover:text-[#2E7D32]">
                <lucide-icon name="x" class="h-5 w-5"></lucide-icon>
              </button>
            </div>
            <div class="space-y-3">
              <div><span class="font-medium text-[#2E7D32]">ID:</span> {{ selectedSubscription.id }}</div>
              <div><span class="font-medium text-[#2E7D32]">Tenant:</span> {{ selectedSubscription.tenantName }} ({{ selectedSubscription.tenantSlug }})</div>
              <div><span class="font-medium text-[#2E7D32]">Plan:</span> {{ selectedSubscription.planName }} ({{ selectedSubscription.planCode }})</div>
              <div><span class="font-medium text-[#2E7D32]">Estado:</span> 
                <span [class]="selectedSubscription.status === 'ACTIVE' ? 'text-[#2E7D32]' : 'text-[#FF9800]'">{{ selectedSubscription.status }}</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Trial:</span> {{ selectedSubscription.trial ? 'Sí' : 'No' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Inicio:</span> {{ selectedSubscription.startedAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Expira:</span> {{ selectedSubscription.expiresAt | date:'medium' }}</div>
              <div><span class="font-medium text-[#2E7D32]">Días restantes:</span> 
                <span [class]="selectedSubscription.remainingDays <= 7 ? 'text-[#FF9800] font-bold' : ''">{{ selectedSubscription.remainingDays }} días</span>
              </div>
              <div><span class="font-medium text-[#2E7D32]">Máx. usuarios:</span> {{ selectedSubscription.maxUsers }}</div>
              <div><span class="font-medium text-[#2E7D32]">Máx. roles:</span> {{ selectedSubscription.maxRoles }}</div>
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
export class PlatformSubscriptionsPageComponent implements OnInit {
  protected listUseCase = inject(PlatformSubscriptionListUseCase);
  protected assignUseCase = inject(PlatformSubscriptionAssignUseCase);
  protected detailUseCase = inject(PlatformSubscriptionGetByIdUseCase);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showAssignForm = false;
  selectedSubscription: any = null;

  ngOnInit(): void {
    this.loadSubscriptions();
  }

  async loadSubscriptions(): Promise<void> {
    await this.listUseCase.loadSubscriptions();
    this.cdr.detectChanges();
  }

  async onAssignSubscription(data: { tenantId: string; planCode: string; overrideTrialDays?: number }): Promise<void> {
    const success = await this.assignUseCase.assignSubscription({
      tenantId: data.tenantId,
      planCode: data.planCode,
      overrideTrialDays: data.overrideTrialDays
    });
    
    if (success) {
      this.toastService.success('Suscripción asignada exitosamente');
      this.showAssignForm = false;
      this.assignUseCase.resetState();
      await this.loadSubscriptions();
    } else {
      this.toastService.error(this.assignUseCase.error() || 'Error al asignar suscripción');
    }
  }

  async viewDetails(id: string): Promise<void> {
    await this.detailUseCase.loadSubscription(id);
    this.selectedSubscription = this.detailUseCase.subscription();
    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.selectedSubscription = null;
    this.detailUseCase.resetState();
    this.cdr.detectChanges();
  }
}