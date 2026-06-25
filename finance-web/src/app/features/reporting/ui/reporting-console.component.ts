import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ReportingService } from '../../../entities/reporting/api/reporting.service';
import {
  PageResponse,
  ReportDefinition,
  ReportExecutionSummary,
  ReportResult,
  ReportingScope
} from '../../../entities/reporting/model/reporting.model';
import { ReportTableComponent } from './report-table.component';
import { ReportBarChartComponent } from './report-bar-chart.component';
import { ReportDonutChartComponent } from './report-donut-chart.component';
import { ReportExportButtonsComponent } from './report-export-buttons.component';

type Tab = 'controlled' | 'ai' | 'history';

@Component({
  selector: 'app-reporting-console',
  standalone: true,
  imports: [CommonModule, FormsModule, ReportTableComponent, ReportBarChartComponent, ReportDonutChartComponent, ReportExportButtonsComponent],
  template: `
    <div class="space-y-6">
      <!-- Header + scope banner -->
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
          <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
          {{ scope() === 'platform' ? 'Plataforma · cross-tenant' : 'Tenant' }}
        </div>
        <h1 class="mt-3 text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">Reportes</h1>
        <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
          Reportes controlados y por IA (texto/voz). Todo lee solo vistas seguras y queda auditado.
        </p>

        <div class="mt-4 flex flex-wrap gap-2">
          <button type="button" (click)="tab.set('controlled')" [class]="tabClass('controlled')">Controlados</button>
          <button type="button" (click)="tab.set('ai')" [class]="tabClass('ai')">IA (texto/voz)</button>
          <button type="button" (click)="openHistory()" [class]="tabClass('history')">Historial</button>
        </div>
      </section>

      <!-- CONTROLADOS -->
      @if (tab() === 'controlled') {
        <section class="grid gap-4 lg:grid-cols-[320px_1fr]">
          <div class="space-y-3">
            @for (def of definitions(); track def.key) {
              <button type="button" (click)="selectDefinition(def)"
                class="w-full cursor-pointer rounded-2xl border p-4 text-left transition-colors"
                [class.border-[#2E7D32]]="selected()?.key === def.key"
                [class.bg-[#F1F8E9]]="selected()?.key === def.key"
                [class.border-[#E8F2E2]]="selected()?.key !== def.key"
                [class.bg-white]="selected()?.key !== def.key">
                <p class="font-bold text-[#1B5E20]">{{ def.title }}</p>
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ def.description }}</p>
              </button>
            } @empty {
              <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
                No hay reportes disponibles.
              </div>
            }
          </div>

          <div class="space-y-4">
            @if (selected(); as def) {
              <div class="rounded-2xl border border-[#E8F2E2] bg-white p-5">
                <h2 class="text-lg font-bold text-[#1B5E20]">{{ def.title }}</h2>
                @if (def.params.length > 0) {
                  <div class="mt-4 grid gap-4 sm:grid-cols-2">
                    @for (param of def.params; track param.name) {
                      <label class="block">
                        <span class="mb-1 block text-sm font-semibold text-[#567157]">
                          {{ prettify(param.name) }}
                        </span>
                        @if (param.options && param.options.length > 0) {
                          <select
                            [ngModel]="paramValue(param.name)"
                            (ngModelChange)="setParam(param.name, $event)"
                            class="w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                            <option value="">Todos</option>
                            @for (opt of param.options; track opt) {
                              <option [value]="opt">{{ prettify(opt) }}</option>
                            }
                          </select>
                        } @else if (param.type === 'BOOLEAN') {
                          <select
                            [ngModel]="paramValue(param.name)"
                            (ngModelChange)="setParam(param.name, $event)"
                            class="w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                            <option value="">Todos</option>
                            <option value="true">Sí</option>
                            <option value="false">No</option>
                          </select>
                        } @else {
                          <input [type]="inputType(param.type)"
                            [ngModel]="paramValue(param.name)"
                            (ngModelChange)="setParam(param.name, $event)"
                            class="w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white">
                        }
                      </label>
                    }
                  </div>
                } @else {
                  <p class="mt-2 text-sm text-[#6B7D6C]">Este reporte no requiere parámetros.</p>
                }
                <button type="button" (click)="runControlled()" [disabled]="loading()"
                  class="mt-4 cursor-pointer rounded-full bg-[#2E7D32] px-6 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#1B5E20] disabled:opacity-50">
                  {{ loading() ? 'Ejecutando…' : 'Ejecutar' }}
                </button>
              </div>
            } @else {
              <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
                Elegí un reporte de la izquierda.
              </div>
            }

            <ng-container [ngTemplateOutlet]="resultBlock"></ng-container>
          </div>
        </section>
      }

      <!-- IA -->
      @if (tab() === 'ai') {
        <section class="space-y-4">
          <div class="rounded-2xl border border-[#E8F2E2] bg-white p-5">
            <label class="block">
              <span class="mb-1 block text-sm font-semibold text-[#567157]">Pedí tu reporte en lenguaje natural</span>
              <textarea [(ngModel)]="aiPrompt" rows="3"
                placeholder="Ej: movimientos completados de este mes ordenados por monto"
                class="w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition-colors focus:border-[#2E7D32] focus:bg-white"></textarea>
            </label>
            <div class="mt-3 flex flex-wrap items-center gap-2">
              <button type="button" (click)="runAiText()" [disabled]="loading() || !aiPrompt().trim()"
                class="cursor-pointer rounded-full bg-[#2E7D32] px-6 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#1B5E20] disabled:opacity-50">
                {{ loading() ? 'Generando…' : 'Generar' }}
              </button>
              @if (!recording()) {
                <button type="button" (click)="startRecording()" [disabled]="loading()"
                  class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-5 py-2.5 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:opacity-50">
                  🎤 Grabar
                </button>
              } @else {
                <button type="button" (click)="stopRecording()"
                  class="cursor-pointer rounded-full border border-red-300 bg-red-50 px-5 py-2.5 text-sm font-semibold text-red-700 transition-colors hover:bg-red-100">
                  ⏹ Detener
                </button>
              }
              <span class="text-xs text-[#9AA89A]">El SQL generado se valida en el servidor antes de ejecutarse.</span>
            </div>
          </div>

          <ng-container [ngTemplateOutlet]="resultBlock"></ng-container>
        </section>
      }

      <!-- HISTORIAL -->
      @if (tab() === 'history') {
        <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-bold text-[#1B5E20]">Historial de ejecuciones</h2>
            <button type="button" (click)="loadHistory(historyPage())"
              class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] hover:bg-[#F1F8E9]">
              Recargar
            </button>
          </div>

          @if (executions(); as page) {
            <div class="mt-4 overflow-x-auto rounded-2xl border border-[#E8F2E2]">
              <table class="min-w-[760px] w-full divide-y divide-[#E8F2E2]">
                <thead class="bg-[#F7FBF3]">
                  <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
                    <th class="px-4 py-3">Reporte</th>
                    <th class="px-4 py-3">Tipo</th>
                    <th class="px-4 py-3">Estado</th>
                    <th class="px-4 py-3">Filas</th>
                    <th class="px-4 py-3">Fecha</th>
                    <th class="px-4 py-3 text-right">Acción</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-[#EEF5EA] bg-white">
                  @for (e of page.content; track e.id) {
                    <tr [style.background-color]="viewedId() === e.id ? '#F1F8E9' : null">
                      <td class="px-4 py-3 text-sm font-semibold text-[#1B5E20]">{{ e.title }}</td>
                      <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ e.kind }}</td>
                      <td class="px-4 py-3">
                        <span class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em]"
                          [class.bg-green-100]="e.status === 'SUCCESS'" [class.text-green-700]="e.status === 'SUCCESS'"
                          [class.bg-red-100]="e.status === 'FAILED'" [class.text-red-700]="e.status === 'FAILED'">
                          {{ e.status }}
                        </span>
                      </td>
                      <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ e.rowCount ?? '—' }}</td>
                      <td class="px-4 py-3 text-sm text-[#4F5D4F]">{{ formatDate(e.createdAt) }}</td>
                      <td class="px-4 py-3 text-right">
                        <div class="flex justify-end gap-2">
                          <button type="button" (click)="viewSnapshot(e)" [disabled]="loading() || e.status !== 'SUCCESS'"
                            class="cursor-pointer rounded-full bg-[#2E7D32] px-3 py-1.5 text-xs font-semibold text-white hover:bg-[#256428] disabled:opacity-50">
                            Ver
                          </button>
                          <button type="button" (click)="rerun(e)" [disabled]="loading()"
                            class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-3 py-1.5 text-xs font-semibold text-[#2E7D32] hover:bg-[#F1F8E9] disabled:opacity-50">
                            Re-ejecutar
                          </button>
                        </div>
                      </td>
                    </tr>
                  } @empty {
                    <tr><td colspan="6" class="px-4 py-6 text-center text-sm text-[#6B7D6C]">Sin ejecuciones.</td></tr>
                  }
                </tbody>
              </table>
            </div>
            <div class="mt-3 flex items-center justify-between text-sm text-[#6B7D6C]">
              <span>Página {{ page.number + 1 }} de {{ page.totalPages || 1 }}</span>
              <div class="flex gap-2">
                <button type="button" (click)="loadHistory(page.number - 1)" [disabled]="page.first"
                  class="rounded-full border border-[#C8E6C9] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] disabled:opacity-40">Anterior</button>
                <button type="button" (click)="loadHistory(page.number + 1)" [disabled]="page.last"
                  class="rounded-full border border-[#C8E6C9] px-3 py-1.5 text-xs font-semibold text-[#2E7D32] disabled:opacity-40">Siguiente</button>
              </div>
            </div>
          }

          @if (viewedId()) {
            <div class="mt-5">
              <ng-container [ngTemplateOutlet]="resultBlock"></ng-container>
            </div>
          }
        </section>
      }
    </div>

    <!-- Shared result block (controlled + ai + history view) -->
    <ng-template #resultBlock>
      @if (error(); as err) {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">{{ err }}</div>
      }
      @if (result(); as res) {
        <div class="space-y-4 rounded-2xl border border-[#E8F2E2] bg-white p-5">
          <div class="flex flex-wrap items-center justify-between gap-3">
            <div>
              @if (resultTitle()) {
                <h3 class="text-lg font-black tracking-tight text-[#1B5E20]">{{ resultTitle() }}</h3>
              }
              @if (res.transcript) {
                <p class="mb-2 rounded-xl bg-[#F1F8E9] px-3 py-2 text-xs text-[#2E7D32]">
                  🎤 Transcripción: "{{ res.transcript }}"
                </p>
              }
              <p class="text-sm font-bold text-[#1B5E20]">{{ res.rowCount }} fila(s)
                @if (res.truncated) { <span class="text-xs font-semibold text-amber-600">· resultado truncado</span> }
              </p>
              @if (res.explanation) { <p class="mt-1 text-xs text-[#6B7D6C]">{{ res.explanation }}</p> }
            </div>
            <app-report-export-buttons [busy]="exporting()" (exportFormat)="exportResult($event)"></app-report-export-buttons>
          </div>
          <div class="grid gap-4 lg:grid-cols-2">
            <app-report-donut-chart [columns]="res.columns" [rows]="res.rows"></app-report-donut-chart>
            <div class="rounded-2xl border border-[#E8F2E2] bg-white p-4">
              <app-report-bar-chart [columns]="res.columns" [rows]="res.rows"></app-report-bar-chart>
            </div>
          </div>
          <app-report-table [columns]="res.columns" [rows]="res.rows"></app-report-table>
        </div>
      }
    </ng-template>
  `
})
export class ReportingConsoleComponent implements OnInit {
  private readonly api = inject(ReportingService);

  readonly scope = input.required<ReportingScope>();

  readonly tab = signal<Tab>('controlled');
  readonly definitions = signal<ReportDefinition[]>([]);
  readonly selected = signal<ReportDefinition | null>(null);
  readonly paramValues = signal<Record<string, string>>({});

  readonly result = signal<ReportResult | null>(null);
  readonly resultTitle = signal<string | null>(null);
  readonly viewedId = signal<string | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly exporting = signal(false);

  readonly aiPrompt = signal('');
  readonly recording = signal(false);
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];

  readonly executions = signal<PageResponse<ReportExecutionSummary> | null>(null);
  readonly historyPage = computed(() => this.executions()?.number ?? 0);

  ngOnInit(): void {
    void this.loadDefinitions();
  }

  tabClass(tab: Tab): string {
    const active = this.tab() === tab;
    return `cursor-pointer rounded-full border border-[#C8E6C9] px-4 py-2 text-sm font-semibold transition-colors ${
      active ? 'bg-[#2E7D32] text-white' : 'bg-white text-[#2E7D32] hover:bg-[#F1F8E9]'}`;
  }

  inputType(type: string): string {
    if (type === 'DATE') return 'date';
    if (type === 'TIMESTAMP') return 'datetime-local';
    if (type === 'NUMBER') return 'number';
    return 'text';
  }

  async loadDefinitions(): Promise<void> {
    try {
      const res = await firstValueFrom(this.api.listDefinitions(this.scope()));
      this.definitions.set(res.data ?? []);
    } catch {
      this.error.set('No se pudieron cargar las definiciones de reportes.');
    }
  }

  selectDefinition(def: ReportDefinition): void {
    this.selected.set(def);
    this.paramValues.set({});
    this.result.set(null);
    this.error.set(null);
  }

  paramValue(name: string): string {
    return this.paramValues()[name] ?? '';
  }

  setParam(name: string, value: string): void {
    this.paramValues.update(current => ({ ...current, [name]: value }));
  }

  async runControlled(): Promise<void> {
    const def = this.selected();
    if (!def) return;
    this.viewedId.set(null);
    this.resultTitle.set(def.title);
    await this.execute(() => firstValueFrom(this.api.run(this.scope(), def.key, this.paramValues())));
  }

  async runAiText(): Promise<void> {
    const prompt = this.aiPrompt().trim();
    if (!prompt) return;
    this.viewedId.set(null);
    this.resultTitle.set('Reporte IA');
    await this.execute(() => firstValueFrom(this.api.aiText(this.scope(), prompt)));
  }

  async startRecording(): Promise<void> {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.audioChunks = [];
      this.mediaRecorder = new MediaRecorder(stream);
      this.mediaRecorder.ondataavailable = (e) => this.audioChunks.push(e.data);
      this.mediaRecorder.onstop = () => {
        stream.getTracks().forEach(t => t.stop());
        const blob = new Blob(this.audioChunks, { type: 'audio/webm' });
        void this.execute(() => firstValueFrom(this.api.aiVoice(this.scope(), blob)));
      };
      this.mediaRecorder.start();
      this.recording.set(true);
    } catch {
      this.error.set('No se pudo acceder al micrófono.');
    }
  }

  stopRecording(): void {
    this.recording.set(false);
    this.mediaRecorder?.stop();
  }

  private async execute(call: () => Promise<{ data: ReportResult }>): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const res = await call();
      this.result.set(res.data);
    } catch (e) {
      this.result.set(null);
      this.error.set(this.errorMessage(e));
    } finally {
      this.loading.set(false);
    }
  }

  async exportResult(format: 'PDF' | 'XLSX'): Promise<void> {
    const res = this.result();
    if (!res) return;
    this.exporting.set(true);
    try {
      const created = await firstValueFrom(this.api.createExport(this.scope(), res.executionId, format));
      const blob = await firstValueFrom(this.api.download(this.scope(), created.data.id));
      const url = window.URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = created.data.fileName;
      anchor.click();
      window.URL.revokeObjectURL(url);
    } catch (e) {
      this.error.set(this.errorMessage(e));
    } finally {
      this.exporting.set(false);
    }
  }

  openHistory(): void {
    this.tab.set('history');
    void this.loadHistory(0);
  }

  async loadHistory(page: number): Promise<void> {
    if (page < 0) return;
    try {
      const res = await firstValueFrom(this.api.listExecutions(this.scope(), page));
      this.executions.set(res.data);
    } catch (e) {
      this.error.set(this.errorMessage(e));
    }
  }

  async rerun(e: ReportExecutionSummary): Promise<void> {
    this.tab.set('controlled');
    this.viewedId.set(null);
    this.resultTitle.set(e.title);
    await this.execute(() => firstValueFrom(this.api.rerun(this.scope(), e.id)));
  }

  /** Show the stored snapshot of a past execution (no re-run). */
  async viewSnapshot(e: ReportExecutionSummary): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const res = await firstValueFrom(this.api.getExecution(this.scope(), e.id));
      const d = res.data;
      this.result.set({
        executionId: d.summary.id,
        kind: d.summary.kind,
        columns: d.columns,
        rows: d.rows,
        rowCount: d.summary.rowCount ?? d.rows.length,
        truncated: d.summary.truncated,
        explanation: null,
        transcript: d.transcript,
        schemaUsed: '',
        limitKind: 'USER'
      });
      this.resultTitle.set(e.title);
      this.viewedId.set(e.id);
    } catch (err) {
      this.error.set(this.errorMessage(err));
    } finally {
      this.loading.set(false);
    }
  }

  formatDate(value: string | null | undefined): string {
    if (!value) return '—';
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? '—'
      : new Intl.DateTimeFormat('es-BO', { dateStyle: 'medium', timeStyle: 'short' }).format(date);
  }

  private static readonly LABELS: Record<string, string> = {
    eventType: 'Tipo de evento', outcome: 'Resultado', status: 'Estado', currency: 'Moneda',
    active: 'Activo', limitType: 'Tipo de límite', entryType: 'Tipo de asiento',
    REPORT_EXPORTED: 'Reporte exportado', REPORT_EXECUTED: 'Reporte ejecutado', REPORT_RERUN: 'Reporte re-ejecutado',
    LOGIN: 'Inicio de sesión', LOGIN_SUCCESS: 'Inicio de sesión', LOGOUT: 'Cierre de sesión',
    TOKEN_REFRESHED: 'Token renovado', PASSWORD_CHANGED: 'Contraseña cambiada',
    PASSWORD_RESET_REQUESTED: 'Reseteo solicitado', PASSWORD_RESET_COMPLETED: 'Reseteo completado',
    USER_CREATED: 'Usuario creado', USER_UPDATED: 'Usuario actualizado',
    USER_ACTIVATED: 'Usuario activado', USER_DEACTIVATED: 'Usuario desactivado', USER_ROLES_ASSIGNED: 'Roles asignados',
    ROLE_CREATED: 'Rol creado', ROLE_UPDATED: 'Rol actualizado', ROLE_ACTIVATED: 'Rol activado', ROLE_DEACTIVATED: 'Rol desactivado',
    ACCOUNT_CREATED: 'Cuenta creada', ACCOUNT_UPDATED: 'Cuenta actualizada', ACCOUNT_ACTIVATED: 'Cuenta activada',
    ACCOUNT_BLOCKED: 'Cuenta bloqueada', ACCOUNT_FROZEN: 'Cuenta congelada', ACCOUNT_CLOSED: 'Cuenta cerrada',
    ACCOUNT_APPROVAL_REQUESTED: 'Aprobación solicitada',
    TRANSACTION_CREATED: 'Transacción creada', TRANSACTION_COMPLETED: 'Transacción completada',
    TRANSACTION_FAILED: 'Transacción fallida', TRANSFER_RECEIVED: 'Transferencia recibida',
    ACCOUNTING_PERIOD_CREATED: 'Periodo contable creado', ACCOUNTING_PERIOD_CLOSED: 'Periodo contable cerrado',
    LIMIT_RULE_CREATED: 'Límite creado', LIMIT_RULE_UPDATED: 'Límite actualizado', LIMIT_RULE_DEACTIVATED: 'Límite desactivado',
    LOAN_REQUESTED: 'Préstamo solicitado', LOAN_APPROVED: 'Préstamo aprobado', LOAN_REJECTED: 'Préstamo rechazado',
    LOAN_DISBURSED: 'Préstamo desembolsado', LOAN_PAYMENT_RECORDED: 'Pago de préstamo', LOAN_PAID_OFF: 'Préstamo pagado',
    SUCCESS: 'Éxito', FAILURE: 'Fallo'
  };

  /** Humanize a camelCase param name or an UPPER_SNAKE option value. */
  prettify(value: string): string {
    if (!value) return '';
    const known = ReportingConsoleComponent.LABELS[value];
    if (known) return known;
    const s = value.replace(/([a-z])([A-Z])/g, '$1 $2').replace(/_/g, ' ').toLowerCase().trim();
    return s.charAt(0).toUpperCase() + s.slice(1);
  }

  private errorMessage(e: unknown): string {
    const err = e as { error?: { message?: string; errors?: string[] }; status?: number };
    if (err?.status === 503) return 'El servicio de IA no está disponible. Probá un reporte controlado.';
    return err?.error?.message || err?.error?.errors?.[0] || 'Ocurrió un error al ejecutar el reporte.';
  }
}
