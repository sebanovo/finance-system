import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { LimitRuleResponse } from '../../entities/limits';
import { LimitRulesListUseCase, LimitRuleFormComponent } from '../../features/limits-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-limits-rules-page',
  standalone: true,
  imports: [CommonModule, LimitRuleFormComponent, PlatformPaginationComponent, HasPermissionPipe, LucideAngularModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Riesgo y límites
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Reglas de límite operativo
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Administra límites transaccionales y reglas preventivas con una vista más clara y compacta.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="loadRules()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="refresh-ccw" [size]="16"></lucide-icon>
              Recargar
            </button>
            <button
              *ngIf="'limits.create' | hasPermission"
              (click)="openCreateForm()"
              type="button"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]">
              <lucide-icon name="plus" [size]="16"></lucide-icon>
              Nueva regla
            </button>
          </div>
        </div>
      </section>

      <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">En vista</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ visibleCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Reglas filtradas</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Activas</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ activeCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Aplicando control</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Con revisión</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ reviewCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Requieren validación manual</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Sin filtros</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ unlimitedCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Reglas amplias o globales</p>
        </div>
      </section>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtro rápido</h2>
            <p class="text-sm text-[#6B7D6C]">Busca por código, nombre, alcance, tipo de transacción, cuenta o moneda.</p>
          </div>
          <label class="relative w-full lg:max-w-md">
            <lucide-icon name="search" class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#6B7D6C]" [size]="16"></lucide-icon>
            <input
              type="text"
              [value]="searchTerm()"
              (input)="onSearch(($any($event.target).value || '').toString())"
              placeholder="Buscar regla..."
              class="w-full rounded-full border border-[#C8E6C9] bg-[#FAFCF8] py-3 pl-10 pr-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#2E7D32] focus:bg-white">
          </label>
        </div>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar reglas</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="loadRules()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando reglas...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Regla</th>
                  <th scope="col" class="px-6 py-4 font-medium">Alcance</th>
                  <th scope="col" class="px-6 py-4 font-medium">Condición</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Límites</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (rule of filteredRules(); track rule.id) {
                  <tr class="transition-colors hover:bg-[#F7FBF3]">
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ rule.name }}</div>
                      <div class="mt-1 text-xs text-[#6B7D6C] break-words">Código: {{ rule.code }}</div>
                      @if (rule.description) {
                        <div class="mt-2 text-xs leading-5 text-[#4F5D4F] break-words">{{ rule.description }}</div>
                      }
                    </td>
                    <td class="px-6 py-4">
                      <div class="font-medium text-[#1B5E20]">{{ scopeTypeLabel(rule.scopeType) }}</div>
                      <div class="mt-1 text-xs text-[#6B7D6C]">{{ periodLabel(rule.period) }}</div>
                      <div class="mt-1 text-xs text-[#6B7D6C]">{{ rule.scopeDescription || 'Alcance general' }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="space-y-2">
                        <span class="inline-flex rounded-full bg-[#F1F8E9] px-2.5 py-1 text-xs font-semibold text-[#2E7D32]">
                          {{ limitTypeLabel(rule.limitType) }}
                        </span>
                        <div class="flex flex-wrap gap-2">
                          @if (rule.transactionType) {
                            <span class="inline-flex rounded-full border border-[#DDEED8] bg-white px-2.5 py-1 text-[11px] font-semibold text-[#4F5D4F]">
                              {{ transactionTypeLabel(rule.transactionType) }}
                            </span>
                          }
                          @if (rule.accountType) {
                            <span class="inline-flex rounded-full border border-[#DDEED8] bg-white px-2.5 py-1 text-[11px] font-semibold text-[#4F5D4F]">
                              {{ accountTypeLabel(rule.accountType) }}
                            </span>
                          }
                          @if (rule.currency) {
                            <span class="inline-flex rounded-full border border-[#DDEED8] bg-white px-2.5 py-1 text-[11px] font-semibold text-[#4F5D4F]">
                              {{ rule.currency }}
                            </span>
                          }
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-semibold text-[#1B5E20]">
                        {{ amountSummary(rule) }}
                      </div>
                      @if (rule.maxCount) {
                        <div class="mt-1 text-xs text-[#6B7D6C]">Máx. {{ rule.maxCount }} operaciones</div>
                      }
                    </td>
                    <td class="px-6 py-4 text-center">
                      <div class="flex flex-col items-center gap-1">
                        <span class="rounded-full px-2.5 py-1 text-xs font-semibold" [ngClass]="rule.active ? 'bg-green-500/10 text-green-700' : 'bg-slate-500/10 text-slate-600'">
                          {{ rule.active ? 'Activa' : 'Inactiva' }}
                        </span>
                        @if (rule.requireReviewExceed) {
                          <span class="rounded-full bg-amber-500/10 px-2.5 py-1 text-[11px] font-semibold text-amber-700">
                            Revisión
                          </span>
                        }
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="flex justify-end gap-2">
                        @if ('limits.detail' | hasPermission) {
                          <button
                            type="button"
                            (click)="openDetail(rule)"
                            class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                            <lucide-icon name="eye" [size]="14"></lucide-icon>
                            Ver
                          </button>
                        }
                        <button
                          *ngIf="'limits.update' | hasPermission"
                          (click)="openEditForm(rule)"
                          type="button"
                          class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
                          <lucide-icon name="pencil" [size]="14"></lucide-icon>
                          Editar
                        </button>
                      </div>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay reglas de límite registradas.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="mt-4">
            <app-platform-pagination
              [currentPage]="currentPage()"
              [totalPages]="totalPages()"
              [totalElements]="totalElements()"
              [isLoading]="useCase.status() === 'loading'"
              (pageChange)="onPageChange($event)">
            </app-platform-pagination>
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

      @if (selectedRuleDetail()) {
        <div class="app-modal-overlay">
          <div class="app-modal-panel app-modal-panel-sm">
            <div class="app-modal-header">
              <div>
                <h2 class="app-modal-title">Detalle de regla</h2>
                <p class="app-modal-subtitle">Resumen operativo de la limitación seleccionada.</p>
              </div>
              <button
                type="button"
                class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#C8E6C9] bg-white p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                (click)="closeDetail()">
                <lucide-icon name="x" [size]="18"></lucide-icon>
              </button>
            </div>

            <div class="app-modal-content-grid">
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Código</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.code }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Nombre</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.name }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Alcance</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ scopeTypeLabel(selectedRuleDetail()?.scopeType || '') }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Periodo</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ periodLabel(selectedRuleDetail()?.period || '') }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Tipo</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ limitTypeLabel(selectedRuleDetail()?.limitType || '') }}</p>
              </div>
              <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Estado</p>
                <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.active ? 'Activa' : 'Inactiva' }}</p>
              </div>
            </div>

            <div class="rounded-2xl border border-[#E8F2E2] bg-white p-4">
              <h3 class="text-sm font-bold text-[#1B5E20]">Restricciones</h3>
              <div class="mt-4 grid gap-3 sm:grid-cols-2">
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Mínimo</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.minAmount ?? 'Sin mínimo' }}</p>
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Máximo</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.maxAmount ?? 'Sin máximo' }}</p>
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Máx. operaciones</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.maxCount ?? 'Sin límite' }}</p>
                </div>
                <div class="rounded-2xl border border-[#E8F2E2] bg-[#F7FBF3] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.12em] text-[#6B7D6C]">Revisión</p>
                  <p class="mt-1 text-sm font-semibold text-[#1B5E20]">{{ selectedRuleDetail()?.requireReviewExceed ? 'Sí' : 'No' }}</p>
                </div>
              </div>
            </div>

            @if (selectedRuleDetail()?.description) {
              <div class="rounded-2xl border border-[#E8F2E2] bg-white p-4">
                <h3 class="text-sm font-bold text-[#1B5E20]">Descripción</h3>
                <p class="mt-2 text-sm leading-6 text-[#4F5D4F] break-words">{{ selectedRuleDetail()?.description }}</p>
              </div>
            }

            <div class="app-modal-footer">
              <button
                type="button"
                class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]"
                (click)="closeDetail()">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class LimitsRulesPageComponent implements OnInit {
  public readonly useCase = inject(LimitRulesListUseCase);
  private readonly toastService = inject(ToastService);
  readonly pageSize = 20;
  readonly currentPage = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);

  isFormOpen = false;
  selectedRule: LimitRuleResponse | null = null;
  readonly selectedRuleDetail = signal<LimitRuleResponse | null>(null);
  readonly searchTerm = signal('');
  isSubmitting = false;

  readonly filteredRules = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const rules = this.useCase.data();
    if (!term) {
      return rules;
    }

    return rules.filter((rule) => {
      return [
        rule.code,
        rule.name,
        rule.description,
        rule.scopeType,
        rule.period,
        rule.limitType,
        rule.transactionType,
        rule.accountType,
        rule.currency,
        rule.scopeDescription
      ]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(term));
    });
  });

  readonly visibleCount = computed(() => this.filteredRules().length);
  readonly activeCount = computed(() => this.filteredRules().filter((rule) => rule.active).length);
  readonly reviewCount = computed(() => this.filteredRules().filter((rule) => rule.requireReviewExceed).length);
  readonly unlimitedCount = computed(() =>
    this.filteredRules().filter((rule) => !rule.minAmount && !rule.maxAmount && !rule.maxCount).length
  );

  ngOnInit() {
    this.loadRules();
  }

  loadRules() {
    this.useCase.loadRules(this.currentPage(), this.pageSize);
  }

  onPageChange(page: number): void {
    this.useCase.loadRules(page, this.pageSize);
  }

  onSearch(value: string): void {
    this.searchTerm.set(value);
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

  openDetail(rule: LimitRuleResponse): void {
    this.selectedRuleDetail.set(rule);
  }

  closeDetail(): void {
    this.selectedRuleDetail.set(null);
  }

  async onFormSubmit(event: { type: 'create' | 'update'; data: any }) {
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

  limitTypeLabel(value: string): string {
    const labels: Record<string, string> = {
      PER_TRANSACTION_AMOUNT: 'Monto por transacción',
      DAILY_AMOUNT: 'Monto diario',
      MONTHLY_AMOUNT: 'Monto mensual',
      DAILY_COUNT: 'Conteo diario',
      MONTHLY_COUNT: 'Conteo mensual',
      MIN_AMOUNT: 'Monto mínimo',
      MAX_BALANCE: 'Balance máximo'
    };

    return labels[value] ?? value;
  }

  scopeTypeLabel(value: string): string {
    const labels: Record<string, string> = {
      TENANT: 'Global del tenant',
      ACCOUNT_TYPE: 'Por tipo de cuenta',
      TRANSACTION_TYPE: 'Por tipo de transacción',
      USER: 'Por usuario',
      ACCOUNT: 'Por cuenta'
    };

    return labels[value] ?? value;
  }

  periodLabel(value: string): string {
    const labels: Record<string, string> = {
      TRANSACTION: 'Por transacción',
      DAILY: 'Diario',
      MONTHLY: 'Mensual'
    };

    return labels[value] ?? value;
  }

  transactionTypeLabel(value: string): string {
    const labels: Record<string, string> = {
      DEPOSIT: 'Depósito',
      WITHDRAWAL: 'Retiro',
      TRANSFER: 'Transferencia',
      PAYMENT: 'Pago',
      REVERSAL: 'Reversión',
      REFUND: 'Reembolso',
      FEE: 'Comisión',
      ADJUSTMENT: 'Ajuste',
      HOLD: 'Retención',
      RELEASE: 'Liberación',
      SETTLEMENT: 'Liquidación'
    };

    return labels[value] ?? value;
  }

  accountTypeLabel(value: string): string {
    const labels: Record<string, string> = {
      WALLET: 'Billetera',
      SAVINGS: 'Ahorros',
      CHECKING: 'Corriente',
      CREDIT_CARD: 'Crédito',
      PREPAID_CARD: 'Prepago',
      LOAN: 'Préstamo'
    };

    return labels[value] ?? value;
  }

  amountSummary(rule: LimitRuleResponse): string {
    if (rule.minAmount !== null && rule.minAmount !== undefined && rule.maxAmount !== null && rule.maxAmount !== undefined) {
      return `${this.currencyValue(rule.minAmount)} - ${this.currencyValue(rule.maxAmount)}`;
    }
    if (rule.maxAmount !== null && rule.maxAmount !== undefined) {
      return `Máx. ${this.currencyValue(rule.maxAmount)}`;
    }
    if (rule.minAmount !== null && rule.minAmount !== undefined) {
      return `Desde ${this.currencyValue(rule.minAmount)}`;
    }
    return 'Sin monto definido';
  }

  private currencyValue(value: number): string {
    return new Intl.NumberFormat('es-BO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }
}
