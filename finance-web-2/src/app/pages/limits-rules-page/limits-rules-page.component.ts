import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { LimitRulesListUseCase, LimitRuleFormComponent } from '../../features/limits-management';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { LimitRuleResponse } from '../../entities/limits';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-limits-rules-page',
  standalone: true,
  imports: [CommonModule, LimitRuleFormComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Reglas de Límite Operativo</h2>
          <p class="text-muted-foreground">Administra los límites transaccionales y reglas de prevención aplicadas a los usuarios y cuentas.</p>
        </div>
        
        <button 
          *ngIf="'limits.create' | hasPermission"
          (click)="openCreateForm()"
          type="button"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <lucide-icon name="plus" [size]="16"></lucide-icon>
          Nueva Regla
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar reglas</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadRules()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando reglas...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Regla</th>
                  <th scope="col" class="px-6 py-4 font-medium">Alcance</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo / Moneda</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Límites (Mín - Máx)</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (rule of useCase.data(); track rule.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4">
                      <div class="font-bold text-foreground">{{ rule.name }}</div>
                      <div class="text-xs text-muted-foreground mt-0.5">Cód: {{ rule.code }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-medium text-foreground">{{ rule.scopeType }}</div>
                      <div class="text-xs text-muted-foreground mt-0.5 flex items-center gap-1">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                        {{ rule.period }}
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="flex items-center gap-2">
                        @if (rule.transactionType) {
                          <span class="px-2 py-0.5 bg-secondary text-secondary-foreground text-[10px] font-bold rounded-sm uppercase tracking-wide">{{ rule.transactionType }}</span>
                        } @else {
                          <span class="text-xs text-muted-foreground italic">Cualquiera</span>
                        }
                        
                        @if (rule.currency) {
                          <span class="px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-bold rounded-sm">{{ rule.currency }}</span>
                        }
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-medium text-foreground">
                        @if (rule.minAmount !== null && rule.maxAmount !== null) {
                          {{ rule.minAmount | number:'1.2-2' }} - {{ rule.maxAmount | number:'1.2-2' }}
                        } @else if (rule.maxAmount !== null) {
                          Máx: {{ rule.maxAmount | number:'1.2-2' }}
                        } @else {
                          Ilimitado
                        }
                      </div>
                      @if (rule.maxCount) {
                        <div class="text-xs text-muted-foreground mt-0.5">Máx {{ rule.maxCount }} ops</div>
                      }
                    </td>
                    <td class="px-6 py-4 text-center space-y-1">
                      <div>
                        <span class="px-2.5 py-1 text-[10px] font-semibold rounded-full"
                              [ngClass]="rule.active ? 'bg-green-500/10 text-green-600' : 'bg-gray-500/10 text-gray-600'">
                          {{ rule.active ? 'ACTIVA' : 'INACTIVA' }}
                        </span>
                      </div>
                      @if (rule.requireReviewExceed) {
                        <div>
                          <span class="px-2 py-0.5 text-[9px] font-semibold rounded bg-amber-500/10 text-amber-600" title="Requiere revisión al exceder">
                            REVISIÓN req.
                          </span>
                        </div>
                      }
                    </td>
                    <td class="px-6 py-4 text-right space-x-3">
                      <button *ngIf="'limits.update' | hasPermission" (click)="openEditForm(rule)" class="text-primary hover:underline text-xs font-medium">Editar</button>
                      <button *ngIf="'limits.delete' | hasPermission" (click)="deleteRule(rule.id)" class="text-destructive hover:underline text-xs font-medium">Eliminar</button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-muted-foreground">
                      No hay reglas de límite registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      @if (isFormOpen) {
        <app-limit-rule-form
          [rule]="selectedRule"
          [loading]="isSubmitting"
          (formSubmit)="onFormSubmit($event)"
          (cancel)="closeForm()"
        ></app-limit-rule-form>
      }
    </div>
  `
})
export class LimitsRulesPageComponent implements OnInit {
  public readonly useCase = inject(LimitRulesListUseCase);
  private readonly toastService = inject(ToastService);

  isFormOpen = false;
  selectedRule: LimitRuleResponse | null = null;
  isSubmitting = false;

  ngOnInit() {
    this.loadRules();
  }

  loadRules() {
    this.useCase.loadRules();
  }

  openCreateForm() {
    this.selectedRule = null;
    this.isFormOpen = true;
  }

  openEditForm(rule: LimitRuleResponse) {
    this.selectedRule = rule;
    this.isFormOpen = true;
  }

  closeForm() {
    this.isFormOpen = false;
    this.selectedRule = null;
  }

  async onFormSubmit(event: { type: 'create' | 'update', data: any }) {
    this.isSubmitting = true;
    try {
      if (event.type === 'create') {
        await this.useCase.createRule(event.data);
        this.toastService.success('Regla de límite creada exitosamente');
      } else {
        await this.useCase.updateRule(this.selectedRule!.id, event.data);
        this.toastService.success('Regla de límite actualizada exitosamente');
      }
      this.closeForm();
    } catch (error: any) {
      let msg = error.error?.message || error.message || 'Error al procesar la solicitud';
      
      // Traducciones amigables para el usuario
      if (msg.includes('already exists')) {
        msg = 'Ya existe una regla de límite con este código único.';
      }
      
      this.toastService.error(msg, 'No se pudo guardar la regla');
    } finally {
      this.isSubmitting = false;
    }
  }

  async deleteRule(id: string) {
    if (confirm('¿Estás seguro de eliminar (desactivar) esta regla de límite?')) {
      const success = await this.useCase.deleteRule(id);
      if (success) {
        this.toastService.success('Regla desactivada exitosamente');
      } else {
        this.toastService.error('No se pudo desactivar la regla');
      }
    }
  }
}
