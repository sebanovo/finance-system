import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { ElementRef, Component, ViewChild, computed, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Plus, ChevronDown, RefreshCcw, Eye, X, CheckCircle, XCircle, AlertTriangle, ArrowDownToLine, ArrowUpFromLine, ArrowRightLeft, Send } from 'lucide-angular';
import QRCode from 'qrcode';
import { BrowserQRCodeReader, IScannerControls } from '@zxing/browser';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PermissionService } from '../../shared/lib/auth/permission.service';
import { MyAccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { TransactionSlideOverComponent, TransactionActionType, MyTransactionsListUseCase } from '../../features/transactions-management';
import { QrTransactionIntentResponse, TransactionResponse } from '../../entities/transactions';

type TransactionStatusFilter = 'ALL' | 'COMPLETED' | 'PENDING' | 'FAILED' | 'REVERSED';
type TransactionTypeFilter = 'ALL' | 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER' | 'PAYMENT';
type ScannedQrIntent = {
  id: string;
  qrPayload: string;
  status: string;
  targetAccountId?: string;
  amount?: number;
  currency?: string;
  externalReference?: string;
  description?: string;
  expiresAt?: string | null;
};

@Component({
  selector: 'app-my-transactions-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TransactionSlideOverComponent,
    PlatformPaginationComponent,
    LucideAngularModule,
    HasPermissionPipe
  ],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Usuario
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Mis movimientos
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Revisa tus transacciones, filtra la actividad de tu página actual y registra nuevas operaciones si tu perfil lo permite.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="reload()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="refresh-ccw" [size]="16"></lucide-icon>
              Recargar
            </button>

            <div class="relative">
              <button
                *ngIf="canOpenCreateMenu()"
                type="button"
                (click)="toggleCreateMenu()"
                class="inline-flex cursor-pointer items-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428] shadow-sm">
                <lucide-icon name="plus" [size]="16"></lucide-icon>
                Nueva operación
                <lucide-icon name="chevron-down" [size]="14" class="opacity-70"></lucide-icon>
              </button>

              @if (createMenuOpen()) {
                <div class="fixed inset-0 z-10" (click)="createMenuOpen.set(false)"></div>
                <div class="absolute right-0 top-full z-20 mt-2 w-72 overflow-hidden rounded-2xl border border-[#DDEED8] bg-white shadow-xl">
                  <div class="border-b border-[#EEF5EA] px-4 py-2 text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">
                    Operaciones disponibles
                  </div>
                  <button *ngIf="'me.transactions.deposit' | hasPermission" type="button" (click)="openSlideOver('deposit')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-down-to-line" [size]="16"></lucide-icon>
                    Depósito
                  </button>
                  <button *ngIf="'me.transactions.withdrawal' | hasPermission" type="button" (click)="openSlideOver('withdrawal')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-up-from-line" [size]="16"></lucide-icon>
                    Retiro
                  </button>
                  <button *ngIf="'me.transactions.transfer' | hasPermission" type="button" (click)="openSlideOver('transfer')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-right-left" [size]="16"></lucide-icon>
                    Transferencia
                  </button>
                  <button *ngIf="'me.transactions.payment' | hasPermission" type="button" (click)="openSlideOver('payment')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="send" [size]="16"></lucide-icon>
                    Pago
                  </button>
                  <button *ngIf="'me.transactions.hold' | hasPermission" type="button" (click)="openSlideOver('hold')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="alert-triangle" [size]="16"></lucide-icon>
                    Retención
                  </button>
                  <button *ngIf="'me.transactions.release' | hasPermission" type="button" (click)="openSlideOver('release')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="rotate-ccw" [size]="16"></lucide-icon>
                    Liberación
                  </button>
                  <button *ngIf="'me.transactions.qr.create' | hasPermission" type="button" (click)="openSlideOver('qr-intent')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="send" [size]="16"></lucide-icon>
                    Cobro por QR
                  </button>
                  <button *ngIf="'me.transactions.qr.confirm' | hasPermission" type="button" (click)="openQrPaymentScanner()" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="send" [size]="16"></lucide-icon>
                    Pago por QR
                  </button>
                </div>
              }
            </div>
          </div>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">En página</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().total }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Movimientos visibles actualmente</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Completados</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().completed }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Procesados correctamente</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Pendientes</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().pending }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">En revisión o cola</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Monto visible</p>
          <p class="mt-4 text-2xl font-black text-[#1B5E20]">{{ formatMoney(transactionStats().totalAmount, transactionStats().currency) }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Suma de la página actual</p>
        </div>
      </section>

      <section class="grid gap-4 xl:grid-cols-3">
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Distribución</p>
              <h3 class="mt-1 text-lg font-bold text-[#1B5E20]">Por estado</h3>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-semibold text-[#2E7D32]">Vista actual</span>
          </div>
          <div class="mt-4 space-y-3">
            @for (item of statusDistribution(); track item.code) {
              <div class="space-y-1">
                <div class="flex items-center justify-between text-sm">
                  <span class="font-medium text-[#4F5D4F]">{{ item.label }}</span>
                  <span class="font-semibold text-[#1B5E20]">{{ item.total }}</span>
                </div>
                <div class="h-2 overflow-hidden rounded-full bg-[#EEF5EA]">
                  <div class="h-full rounded-full bg-[#2E7D32]" [style.width.%]="distributionWidth(item.total, statusDistributionMax())"></div>
                </div>
              </div>
            }
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Distribución</p>
              <h3 class="mt-1 text-lg font-bold text-[#1B5E20]">Por tipo</h3>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-semibold text-[#2E7D32]">Vista actual</span>
          </div>
          <div class="mt-4 space-y-3">
            @for (item of typeDistribution(); track item.code) {
              <div class="space-y-1">
                <div class="flex items-center justify-between text-sm">
                  <span class="font-medium text-[#4F5D4F]">{{ item.label }}</span>
                  <span class="font-semibold text-[#1B5E20]">{{ item.total }}</span>
                </div>
                <div class="h-2 overflow-hidden rounded-full bg-[#EEF5EA]">
                  <div class="h-full rounded-full bg-[#7CB342]" [style.width.%]="distributionWidth(item.total, typeDistributionMax())"></div>
                </div>
              </div>
            }
          </div>
        </div>

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Tendencia</p>
              <h3 class="mt-1 text-lg font-bold text-[#1B5E20]">Actividad reciente</h3>
            </div>
            <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-semibold text-[#2E7D32]">Últimos días</span>
          </div>
          <div class="mt-4 space-y-3">
            @for (item of recentTrend(); track item.label) {
              <div class="space-y-1">
                <div class="flex items-center justify-between text-sm">
                  <span class="font-medium text-[#4F5D4F]">{{ item.label }}</span>
                  <span class="font-semibold text-[#1B5E20]">{{ item.total }}</span>
                </div>
                <div class="flex h-2 overflow-hidden rounded-full bg-[#EEF5EA]">
                  <div class="h-full rounded-full bg-[#A5D6A7]" [style.width.%]="distributionWidth(item.total, recentTrendMax())"></div>
                </div>
                <p class="text-[11px] text-[#6B7D6C]">{{ formatMoney(item.amount, item.currency) }}</p>
              </div>
            }
          </div>
        </div>
      </section>

      <section class="rounded-[24px] border border-[#DDEED8] bg-white p-4 sm:p-6 shadow-sm">
        <div class="grid gap-3 lg:grid-cols-[1.2fr_0.8fr_0.8fr]">
          <label class="space-y-2">
            <span class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Buscar en la página actual</span>
            <input
              type="text"
              [(ngModel)]="searchTermModel"
              (ngModelChange)="searchTerm.set($event)"
              placeholder="Tipo, cuentas, referencia o descripción"
              class="flex h-11 w-full rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
          </label>

          <label class="space-y-2">
            <span class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Estado</span>
            <select
              [(ngModel)]="statusFilterModel"
              (ngModelChange)="statusFilter.set($event)"
              class="flex h-11 w-full rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
              <option value="ALL">Todos</option>
              <option value="COMPLETED">Completados</option>
              <option value="PENDING">Pendientes</option>
              <option value="FAILED">Fallidos</option>
              <option value="REVERSED">Revertidos</option>
            </select>
          </label>

          <label class="space-y-2">
            <span class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Tipo</span>
            <select
              [(ngModel)]="typeFilterModel"
              (ngModelChange)="typeFilter.set($event)"
              class="flex h-11 w-full rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
              <option value="ALL">Todos</option>
              <option value="DEPOSIT">Depósitos</option>
              <option value="WITHDRAWAL">Retiros</option>
              <option value="TRANSFER">Transferencias</option>
              <option value="PAYMENT">Pagos</option>
            </select>
          </label>
        </div>
        <p class="mt-3 text-xs text-[#6B7D6C]">Los filtros se aplican sobre la página cargada, no sobre todo el historial.</p>
      </section>

      @if (qrPaymentScannerOpen()) {
        <div class="fixed inset-0 z-[110] flex items-start justify-center p-4 sm:items-center">
          <div class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity" (click)="closeQrPaymentScanner()"></div>
          <div class="relative w-full max-w-5xl max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)]">
            <button type="button" (click)="closeQrPaymentScanner()" class="absolute right-4 top-4 cursor-pointer rounded-full p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
              <lucide-icon name="x" [size]="20"></lucide-icon>
            </button>

            <div class="flex items-center gap-3 border-b border-[#EEF5EA] pb-5">
              <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
                <lucide-icon name="send" [size]="24"></lucide-icon>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Pago por QR</p>
                <h3 class="mt-1 text-2xl font-black text-[#1B5E20]">Escanea y paga con tu cámara</h3>
                <p class="mt-1 text-sm text-[#5F6F5F]">Alinea el QR, revisa el detalle detectado y confirma el pago desde tu cuenta.</p>
              </div>
            </div>

            <div class="mt-6 grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
              <div class="space-y-4">
                <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                  <div class="flex items-center justify-between gap-3">
                    <div>
                      <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Cámara</p>
                      <h4 class="mt-1 text-lg font-bold text-[#1B5E20]">Alinea el código QR</h4>
                    </div>
                    <button
                      type="button"
                      (click)="openQrPaymentScanner()"
                      class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#DDEED8] bg-white px-3 py-2 text-xs font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                      Reiniciar
                    </button>
                  </div>

                  <div class="mt-4 overflow-hidden rounded-2xl border border-dashed border-[#C8E6C9] bg-black/5">
                    <video #qrPaymentVideo class="h-[320px] w-full bg-black object-cover" autoplay muted playsinline></video>
                  </div>

                  <p class="mt-3 text-sm text-[#5F6F5F]">
                    Cuando el QR sea reconocido, el detalle aparecerá a la derecha. Si tu cámara no funciona, pega el texto o enlace del QR debajo.
                  </p>

                  @if (qrPaymentScannerLoading()) {
                    <div class="mt-3 rounded-2xl border border-[#DDEED8] bg-white p-3 text-sm text-[#6B7D6C]">
                      Iniciando cámara...
                    </div>
                  }

                  @if (qrPaymentScannerError()) {
                    <div class="mt-3 rounded-2xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                      {{ qrPaymentScannerError() }}
                    </div>
                  }
                </div>

                <div class="rounded-2xl border border-[#DDEED8] bg-white p-4">
                  <div class="flex items-center justify-between gap-3">
                    <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Carga manual</p>
                    <button
                      type="button"
                      (click)="loadQrPaymentIntentFromManual()"
                      class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-xs font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                      Cargar QR
                    </button>
                  </div>

                  <textarea
                    [(ngModel)]="qrPaymentManualPayloadModel"
                    rows="4"
                    placeholder="Pega el texto del QR, el enlace o el identificador"
                    class="mt-3 flex w-full resize-none rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white"></textarea>
                  <p class="mt-2 text-xs text-[#6B7D6C]">Esta opción es útil si el lector de cámara no está disponible.</p>
                </div>
              </div>

              <div class="space-y-4">
                <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Cuenta origen</p>
                  <select
                    [(ngModel)]="qrPaymentSourceAccountIdModel"
                    (ngModelChange)="qrPaymentSourceAccountId.set($event)"
                    class="mt-3 flex h-11 w-full rounded-full border border-[#DDEED8] bg-white px-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white">
                    <option value="">Selecciona una cuenta</option>
                    <option *ngFor="let acc of accountOptions()" [value]="acc.id">
                      {{ acc.accountNumber }} - {{ acc.customAlias || acc.accountNameLabel }} ({{ acc.currency }})
                    </option>
                  </select>
                  <p class="mt-2 text-xs text-[#6B7D6C]">Esta cuenta será debitada al confirmar el pago.</p>
                </div>

                <div *ngIf="qrPaymentIntent() as qrIntent; else qrPaymentEmpty" class="rounded-2xl border border-[#DDEED8] bg-white p-4">
                  <div class="flex items-start justify-between gap-3">
                    <div>
                      <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Intención detectada</p>
                      <h4 class="mt-1 text-lg font-bold text-[#1B5E20]">
                        {{ qrIntent.status === 'PENDING' ? 'Pago listo para confirmar' : 'QR detectado' }}
                      </h4>
                    </div>
                    <span class="rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-wider" [ngClass]="transactionStatusClass(qrIntent.status)">
                      {{ statusLabel(qrIntent.status) }}
                    </span>
                  </div>

                  <div class="mt-4 grid gap-3 sm:grid-cols-2">
                    <div><p class="text-xs text-[#6B7D6C]">Intento QR</p><p class="font-semibold text-[#1B5E20] break-all">{{ qrIntent.id }}</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Estado</p><p class="font-semibold text-[#1B5E20]">Listo para confirmar</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Cuenta destino</p><p class="font-semibold text-[#1B5E20]">{{ qrIntent.targetAccountId || 'Se detectó el cobro' }}</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Monto</p><p class="font-semibold text-[#1B5E20]">{{ qrIntent.amount !== undefined && qrIntent.currency ? formatMoney(qrIntent.amount, qrIntent.currency) : 'Disponible al confirmar' }}</p></div>
                  </div>

                  <div class="mt-4 rounded-2xl bg-[#F7FBF3] p-3">
                    <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Descripción</p>
                    <p class="mt-1 text-sm text-[#355A38]">{{ qrIntent.description || 'Cobro detectado desde el QR' }}</p>
                  </div>

                  <div class="mt-4 flex flex-wrap justify-end gap-3">
                    <button
                      type="button"
                      (click)="copyQrPayload(qrIntent.qrPayload)"
                      class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-4 py-2 text-sm font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                      <lucide-icon name="copy" [size]="16"></lucide-icon>
                      Copiar QR
                    </button>
                    <button
                      type="button"
                      (click)="confirmQrPayment()"
                      [disabled]="qrPaymentConfirming() || qrIntent.status !== 'PENDING' || !qrPaymentSourceAccountId().trim()"
                      class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428] disabled:opacity-50">
                      <lucide-icon *ngIf="!qrPaymentConfirming()" name="send" [size]="16"></lucide-icon>
                      <svg *ngIf="qrPaymentConfirming()" class="h-4 w-4 animate-spin text-white" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                      </svg>
                      {{ qrPaymentConfirming() ? 'Procesando...' : 'Pagar ahora' }}
                    </button>
                  </div>
                </div>

                <ng-template #qrPaymentEmpty>
                  <div class="rounded-2xl border border-dashed border-[#DDEED8] bg-white p-6 text-sm text-[#6B7D6C]">
                    Escanea un QR real para ver el detalle del cobro y confirmar el pago.
                  </div>
                </ng-template>
              </div>
            </div>

            <div class="mt-6 flex justify-end border-t border-[#EEF5EA] pt-5">
              <button type="button" (click)="closeQrPaymentScanner()" class="inline-flex cursor-pointer items-center justify-center rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      }

      @if (qrIntentModalOpen() && qrIntentResult(); as qrIntent) {
        <div class="fixed inset-0 z-[120] flex items-start justify-center p-4 sm:items-center">
          <div class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity" (click)="closeQrIntentModal()"></div>
          <div class="relative w-full max-w-2xl max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)]">
            <button type="button" (click)="closeQrIntentModal()" class="absolute right-4 top-4 cursor-pointer rounded-full p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
              <lucide-icon name="x" [size]="20"></lucide-icon>
            </button>

            <div class="flex items-center gap-3 border-b border-[#EEF5EA] pb-5">
              <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
                <lucide-icon name="send" [size]="24"></lucide-icon>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Cobro por QR creado</p>
                <h3 class="mt-1 text-2xl font-black text-[#1B5E20]">Comparte este QR para cobrar</h3>
                <p class="mt-1 text-sm text-[#5F6F5F]">La otra persona puede escanearlo con su cámara y completar el pago al instante.</p>
              </div>
            </div>

            <div class="mt-6 grid gap-4 md:grid-cols-2">
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Cuenta destino</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ qrIntent.targetAccountId }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Vence</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ formatDate(qrIntent.expiresAt) }} {{ formatTime(qrIntent.expiresAt) }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Monto</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ formatMoney(qrIntent.amount, qrIntent.currency) }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Descripción</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ qrIntent.description || 'Sin descripción' }}</p>
              </div>
            </div>

            <div class="mt-4 grid gap-4 lg:grid-cols-[auto_1fr]">
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">QR escaneable</p>
                <div class="mt-3 flex min-h-64 w-64 items-center justify-center rounded-2xl bg-[#F7FBF3] p-4">
                  @if (qrIntentQrLoading()) {
                    <div class="text-sm text-[#6B7D6C]">Generando QR...</div>
                  } @else if (qrIntentQrDataUrl(); as qrDataUrl) {
                    <img [src]="qrDataUrl" alt="Código QR para escanear" class="h-auto w-full max-w-56 rounded-xl bg-white p-2 shadow-sm" />
                  } @else {
                    <div class="text-center text-sm text-[#6B7D6C]">No se pudo generar el QR.</div>
                  }
                </div>
              </div>

              <div class="rounded-2xl border border-[#DDEED8] bg-white p-4">
                <div class="flex items-center justify-between gap-3">
                  <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">QR Payload</p>
                  <button type="button" (click)="copyQrPayload(qrIntent.qrPayload)" class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#DDEED8] bg-[#FAFCF8] px-3 py-2 text-xs font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="copy" [size]="14"></lucide-icon>
                    Copiar
                  </button>
                </div>
                <pre class="mt-3 max-h-64 overflow-y-auto overflow-x-hidden whitespace-pre-wrap break-words rounded-2xl bg-[#F7FBF3] p-4 text-xs leading-5 text-[#355A38]">{{ qrIntent.qrPayload }}</pre>
              </div>
            </div>

            <div class="mt-6 flex justify-end border-t border-[#EEF5EA] pt-5">
              <button type="button" (click)="closeQrIntentModal()" class="inline-flex cursor-pointer items-center justify-center rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      }

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar tus movimientos</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="reload()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando movimientos...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        @if (useCase.page(); as page) {
          <div class="flex flex-col gap-3 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#567157] sm:flex-row sm:items-center sm:justify-between">
            <p>
              <span class="font-semibold text-[#1B5E20]">{{ page.totalElements }}</span>
              registro(s) paginados
            </p>
            <p>
              Página <span class="font-semibold text-[#1B5E20]">{{ page.number + 1 }}</span>
              de <span class="font-semibold text-[#1B5E20]">{{ page.totalPages }}</span>
            </p>
          </div>
        }

        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="hidden overflow-x-auto md:block">
            <table class="w-full text-left text-sm">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo / Canal</th>
                  <th scope="col" class="px-6 py-4 font-medium">Cuentas</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Monto</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Fecha</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center w-16">Acciones</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (tx of visibleTransactions(); track tx.id) {
                  <tr
                    [ngClass]="canViewTransactionDetail ? 'cursor-pointer transition-colors hover:bg-[#F7FBF3]' : 'transition-colors'"
                    (click)="canViewTransactionDetail && openDetail(tx)">
                    <td class="px-6 py-4">
                      <div class="font-semibold text-[#1B5E20]">{{ typeLabel(tx.type) }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ tx.channel }}</div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="space-y-1">
                        <div class="text-xs">
                          <span class="text-[#6B7D6C]">Origen:</span>
                          <span class="font-medium text-[#1B5E20]">{{ tx.sourceAccountDisplayName || tx.sourceAccountNumber || 'N/A' }}</span>
                        </div>
                        <div class="text-xs">
                          <span class="text-[#6B7D6C]">Destino:</span>
                          <span class="font-medium text-[#1B5E20]">{{ tx.targetAccountDisplayName || tx.targetAccountNumber || 'N/A' }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="font-black text-[#1B5E20]">{{ formatMoney(tx.amount, tx.currency) }}</div>
                      <div class="text-[11px] text-[#6B7D6C]">{{ tx.currency }}</div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      <span class="inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-semibold uppercase tracking-wider" [ngClass]="transactionStatusClass(tx.status)">
                        @if (tx.status === 'COMPLETED') {
                          <lucide-icon name="check-circle" [size]="12"></lucide-icon>
                        } @else if (tx.status === 'FAILED' || tx.status === 'REJECTED') {
                          <lucide-icon name="x-circle" [size]="12"></lucide-icon>
                        } @else if (tx.status === 'PENDING' || tx.status === 'HELD') {
                          <lucide-icon name="clock" [size]="12"></lucide-icon>
                        } @else {
                          <lucide-icon name="alert-triangle" [size]="12"></lucide-icon>
                        }
                        {{ statusLabel(tx.status) }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="text-sm text-[#1B5E20]">{{ formatDate(tx.processedAt || tx.createdAt) }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ formatTime(tx.processedAt || tx.createdAt) }}</div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      @if (canViewTransactionDetail) {
                        <button type="button" (click)="$event.stopPropagation(); openDetail(tx)" class="cursor-pointer rounded-md p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
                          <lucide-icon name="eye" [size]="16"></lucide-icon>
                        </button>
                      }
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay movimientos para mostrar en esta página.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="grid gap-3 md:hidden">
            @for (tx of visibleTransactions(); track tx.id) {
              <article
                [ngClass]="canViewTransactionDetail ? 'cursor-pointer rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4 shadow-sm transition-colors hover:bg-[#F7FBF3]' : 'rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4 shadow-sm transition-colors'"
                (click)="canViewTransactionDetail && openDetail(tx)">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="text-sm font-semibold text-[#1B5E20]">{{ typeLabel(tx.type) }}</p>
                    <p class="text-xs text-[#6B7D6C]">{{ tx.channel }}</p>
                  </div>
                  <span class="inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-[11px] font-semibold uppercase tracking-wider" [ngClass]="transactionStatusClass(tx.status)">
                    {{ statusLabel(tx.status) }}
                  </span>
                </div>

                <div class="mt-3 flex items-end justify-between gap-3">
                  <div class="min-w-0">
                    <p class="truncate text-sm font-medium text-[#4F5D4F]">
                      {{ tx.sourceAccountDisplayName || tx.sourceAccountNumber || 'N/A' }} → {{ tx.targetAccountDisplayName || tx.targetAccountNumber || 'N/A' }}
                    </p>
                    <p class="mt-1 text-xs text-[#6B7D6C]">{{ formatDate(tx.processedAt || tx.createdAt) }} · {{ formatTime(tx.processedAt || tx.createdAt) }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-lg font-black text-[#1B5E20]">{{ formatMoney(tx.amount, tx.currency) }}</p>
                    <p class="text-[11px] text-[#6B7D6C]">{{ tx.currency }}</p>
                  </div>
                </div>

                <div class="mt-3 flex justify-end" (click)="$event.stopPropagation()">
                  @if (canViewTransactionDetail) {
                    <button type="button" (click)="openDetail(tx)" class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#DDEED8] bg-white px-3 py-2 text-xs font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                      <lucide-icon name="eye" [size]="14"></lucide-icon>
                      Ver detalle
                    </button>
                  }
                </div>
              </article>
            } @empty {
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-8 text-center text-sm text-[#6B7D6C]">
                No hay movimientos para mostrar en esta página.
              </div>
            }
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
    </div>

    <app-transaction-slide-over
      [isOpen]="isSlideOverOpen()"
      [transactionType]="selectedTransactionType()"
      [accounts]="accountOptions()"
      [accountsLoading]="accountsLoading()"
      (closed)="isSlideOverOpen.set(false)"
      (saved)="onTransactionSaved($event)">
    </app-transaction-slide-over>

    @if (selectedTransaction(); as detail) {
      <div class="fixed inset-0 z-[100] flex items-start justify-center p-4 sm:items-center">
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity" (click)="closeDetail()"></div>
        <div class="relative w-full max-w-4xl max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)]">
          <button type="button" (click)="closeDetail()" class="absolute right-4 top-4 cursor-pointer rounded-full p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>

          <div class="space-y-6">
            <div class="flex flex-col gap-4 border-b border-[#EEF5EA] pb-5 lg:flex-row lg:items-end lg:justify-between">
              <div>
                <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Detalle de movimiento</p>
                <h3 class="mt-2 text-2xl font-black text-[#1B5E20]">{{ typeLabel(detail.type) }}</h3>
                <p class="mt-1 text-sm text-[#5F6F5F]">{{ detail.description || 'Sin descripción' }}</p>
              </div>
              <div class="flex flex-wrap items-center gap-2">
                <span class="inline-flex items-center gap-1 rounded-full px-3 py-1 text-xs font-semibold uppercase tracking-wider" [ngClass]="transactionStatusClass(detail.status)">
                  {{ statusLabel(detail.status) }}
                </span>
                <span class="rounded-full bg-[#F1F8E9] px-3 py-1 text-xs font-semibold text-[#2E7D32]">{{ detail.channel }}</span>
              </div>
            </div>

            <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Monto</p>
                <p class="mt-2 text-2xl font-black text-[#1B5E20]">{{ formatMoney(detail.amount, detail.currency) }}</p>
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ detail.currency }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Origen</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ detail.sourceAccountDisplayName || detail.sourceAccountNumber || 'N/A' }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Destino</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ detail.targetAccountDisplayName || detail.targetAccountNumber || 'N/A' }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Fechas</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ formatDate(detail.processedAt || detail.createdAt) }}</p>
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ formatTime(detail.processedAt || detail.createdAt) }}</p>
              </div>
            </div>

            <div class="grid gap-4 lg:grid-cols-2">
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-5">
                <h4 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Información útil</h4>
                <div class="mt-4 grid gap-3 sm:grid-cols-2">
                  <div><p class="text-xs text-[#6B7D6C]">Referencia externa</p><p class="font-medium text-[#1B5E20]">{{ detail.externalReference || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Idempotency key</p><p class="font-medium text-[#1B5E20] break-all">{{ detail.idempotencyKey || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Detalle de descripción</p><p class="font-medium text-[#1B5E20]">{{ detail.description || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Estado bruto</p><p class="font-medium text-[#1B5E20]">{{ detail.status }}</p></div>
                </div>
              </div>

              <div class="rounded-2xl border border-[#DDEED8] bg-white p-5">
                <h4 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">FX / Incidencias</h4>
                @if (detail.fxDetail) {
                  <div class="mt-4 grid gap-3 sm:grid-cols-2">
                    <div><p class="text-xs text-[#6B7D6C]">Tasa</p><p class="font-semibold text-[#1B5E20]">{{ detail.fxDetail.exchangeRate | number:'1.6-6' }}</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Original</p><p class="font-semibold text-[#1B5E20]">{{ detail.fxDetail.originalAmount | number:'1.2-2' }} {{ detail.fxDetail.sourceCurrency }}</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Convertido</p><p class="font-semibold text-[#1B5E20]">{{ detail.fxDetail.convertedAmount | number:'1.2-2' }} {{ detail.fxDetail.targetCurrency }}</p></div>
                    <div><p class="text-xs text-[#6B7D6C]">Monedas</p><p class="font-semibold text-[#1B5E20]">{{ detail.fxDetail.sourceCurrency }} → {{ detail.fxDetail.targetCurrency }}</p></div>
                  </div>
                } @else {
                  <p class="mt-4 text-sm text-[#6B7D6C]">No hay detalle de FX para este movimiento.</p>
                }

                @if (detail.failureReason) {
                  <div class="mt-4 rounded-2xl border border-red-200 bg-red-50 p-4">
                    <p class="text-xs font-semibold uppercase tracking-[0.14em] text-red-700">Motivo de falla</p>
                    <p class="mt-1 text-sm text-red-700">{{ detail.failureReason }}</p>
                  </div>
                }
              </div>
            </div>

            @if (detail.movements?.length) {
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-5">
                <h4 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Movimientos contables</h4>
                <div class="mt-4 grid gap-3 md:grid-cols-2 xl:grid-cols-3">
                  @for (movement of detail.movements ?? []; track movement.accountId) {
                    <div class="rounded-2xl border border-[#EEF5EA] bg-[#FAFCF8] p-4">
                      <p class="text-sm font-semibold text-[#1B5E20]">{{ movement.accountDisplayName || movement.accountNumber }}</p>
                      <p class="mt-1 text-xs text-[#6B7D6C]">{{ movement.movementType }}</p>
                      <div class="mt-3 flex items-center justify-between text-sm">
                        <span class="text-[#567157]">Monto</span>
                        <span class="font-semibold text-[#1B5E20]">{{ formatMoney(movement.amount, movement.currency) }}</span>
                      </div>
                      <div class="mt-2 flex items-center justify-between text-sm">
                        <span class="text-[#567157]">Saldo después</span>
                        <span class="font-semibold text-[#1B5E20]">{{ formatMoney(movement.balanceAfter, movement.currency) }}</span>
                      </div>
                    </div>
                  }
                </div>
              </div>
            }

            <div class="flex justify-end border-t border-[#EEF5EA] pt-5">
              <button type="button" (click)="closeDetail()" class="inline-flex cursor-pointer items-center justify-center rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      </div>
    }
  `
})
export class MyTransactionsPageComponent implements OnInit, OnDestroy {
  public readonly useCase = inject(MyTransactionsListUseCase);
  public readonly accountListUseCase = inject(MyAccountListUseCase);
  private readonly toastService = inject(ToastService);
  private readonly datePipe = inject(DatePipe);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly permissionService = inject(PermissionService);
  private readonly qrReader = new BrowserQRCodeReader();
  private qrScannerControls: IScannerControls | null = null;
  @ViewChild('qrPaymentVideo') qrPaymentVideo?: ElementRef<HTMLVideoElement>;

  readonly pageSize = 20;
  readonly isSlideOverOpen = signal(false);
  readonly selectedTransactionType = signal<TransactionActionType>('deposit');
  readonly selectedTransaction = signal<TransactionResponse | null>(null);
  readonly qrIntentModalOpen = signal(false);
  readonly qrIntentResult = signal<QrTransactionIntentResponse | null>(null);
  readonly qrIntentQrDataUrl = signal<string | null>(null);
  readonly qrIntentQrLoading = signal(false);
  readonly qrPaymentScannerOpen = signal(false);
  readonly qrPaymentScannerLoading = signal(false);
  readonly qrPaymentScannerError = signal<string | null>(null);
  readonly qrPaymentIntent = signal<ScannedQrIntent | null>(null);
  readonly qrPaymentSourceAccountId = signal('');
  qrPaymentSourceAccountIdModel = '';
  qrPaymentManualPayloadModel = '';
  readonly qrPaymentConfirming = signal(false);
  readonly createMenuOpen = signal(false);
  readonly searchTerm = signal('');
  readonly statusFilter = signal<TransactionStatusFilter>('ALL');
  readonly typeFilter = signal<TransactionTypeFilter>('ALL');

  searchTermModel = '';
  statusFilterModel: TransactionStatusFilter = 'ALL';
  typeFilterModel: TransactionTypeFilter = 'ALL';

  readonly currentPage = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);
  readonly accountOptions = computed(() => this.accountListUseCase.data());
  readonly accountsLoading = computed(() => this.accountListUseCase.status() === 'loading');
  readonly pageTransactions = computed(() => this.useCase.data());

  readonly visibleTransactions = computed(() => {
    const q = this.searchTerm().trim().toLowerCase();
    const status = this.statusFilter();
    const type = this.typeFilter();

    return this.pageTransactions().filter((tx) => {
      const matchesStatus = status === 'ALL' || tx.status === status;
      const matchesType = type === 'ALL' || tx.type === type;
      const haystack = [
        tx.type,
        tx.status,
        tx.channel,
        tx.amount?.toString(),
        tx.currency,
        tx.sourceAccountNumber,
        tx.sourceAccountDisplayName,
        tx.targetAccountNumber,
        tx.targetAccountDisplayName,
        tx.description,
        tx.externalReference,
        tx.failureReason
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return matchesStatus && matchesType && (!q || haystack.includes(q));
    });
  });

  readonly transactionStats = computed(() => {
    const txs = this.pageTransactions();
    const completed = txs.filter((tx) => tx.status === 'COMPLETED').length;
    const pending = txs.filter((tx) => ['PENDING', 'HELD', 'PENDING_REVIEW', 'PROCESSING', 'AUTHORIZED'].includes(tx.status)).length;
    const totalAmount = txs.reduce((sum, tx) => sum + Number(tx.amount || 0), 0);
    const currency = txs.find((tx) => tx.currency)?.currency ?? 'USD';

    return { total: txs.length, completed, pending, totalAmount, currency };
  });

  readonly statusDistribution = computed(() => this.buildStatusDistribution(this.pageTransactions()));
  readonly typeDistribution = computed(() => this.buildTypeDistribution(this.pageTransactions()));
  readonly recentTrend = computed(() => this.buildRecentTrend(this.pageTransactions()));
  readonly statusDistributionMax = computed(() => Math.max(1, ...this.statusDistribution().map((item) => item.total)));
  readonly typeDistributionMax = computed(() => Math.max(1, ...this.typeDistribution().map((item) => item.total)));
  readonly recentTrendMax = computed(() => Math.max(1, ...this.recentTrend().map((item) => item.total)));

  get canViewTransactionDetail(): boolean {
    return this.permissionService.hasAnyPermission('me.transactions.detail', 'me.transactions.read');
  }

  ngOnInit(): void {
    this.reload();
  }

  async reload(): Promise<void> {
    await Promise.all([
      this.useCase.loadTransactions(this.currentPage(), this.pageSize),
      this.ensureAccountsLoaded()
    ]);
  }

  onPageChange(page: number): void {
    this.useCase.loadTransactions(page, this.pageSize);
  }

  toggleCreateMenu(): void {
    this.createMenuOpen.update((value) => !value);
  }

  canOpenCreateMenu(): boolean {
      return this.permissionService.hasAnyPermission(
      'me.transactions.deposit',
      'me.transactions.withdrawal',
      'me.transactions.transfer',
      'me.transactions.payment',
      'me.transactions.hold',
      'me.transactions.release',
      'me.transactions.qr.create',
      'me.transactions.qr.confirm'
    );
  }

  openSlideOver(type: TransactionActionType): void {
    this.selectedTransactionType.set(type);
    this.isSlideOverOpen.set(true);
    this.createMenuOpen.set(false);
  }

  async onTransactionSaved(event: { type: TransactionActionType; request: any; referenceId?: string }): Promise<void> {
    try {
      if (event.type === 'qr-intent') {
        const result = await this.useCase.createQrIntent(event.request);
        this.qrIntentResult.set(result);
        this.qrIntentModalOpen.set(true);
        await this.generateQrCode(result.qrPayload);
        this.toastService.success('Cobro por QR creado con éxito.');
        this.isSlideOverOpen.set(false);
        this.createMenuOpen.set(false);
        return;
      }

      await this.useCase.createTransaction(event.type, event.request, event.referenceId);
      this.toastService.success('Movimiento procesado con éxito.');
      this.isSlideOverOpen.set(false);
    } catch (error: any) {
      this.toastService.error('Error al procesar el movimiento: ' + (error?.message || error));
    }
  }

  openDetail(transaction: TransactionResponse): void {
    if (!this.canViewTransactionDetail) {
      return;
    }
    this.selectedTransaction.set(transaction);
  }

  closeDetail(): void {
    this.selectedTransaction.set(null);
  }

  closeQrIntentModal(): void {
    this.qrIntentModalOpen.set(false);
    this.qrIntentResult.set(null);
    this.qrIntentQrDataUrl.set(null);
    this.qrIntentQrLoading.set(false);
  }

  async openQrPaymentScanner(): Promise<void> {
    this.createMenuOpen.set(false);
    this.stopQrPaymentScanner();
    this.resetQrPaymentState();

    await this.ensureAccountsLoaded();

    const defaultAccountId = this.accountOptions()[0]?.id ?? '';
    if (!defaultAccountId) {
      this.toastService.error('No tienes cuentas disponibles para pagar por QR.');
      return;
    }

    this.qrPaymentSourceAccountId.set(defaultAccountId);
    this.qrPaymentSourceAccountIdModel = defaultAccountId;
    this.qrPaymentScannerOpen.set(true);

    window.setTimeout(() => void this.startQrPaymentScanner(), 0);
  }

  closeQrPaymentScanner(): void {
    this.stopQrPaymentScanner();
    this.qrPaymentScannerOpen.set(false);
    this.resetQrPaymentState();
  }

  async loadQrPaymentIntentFromManual(): Promise<void> {
    const payload = this.qrPaymentManualPayloadModel.trim();
    if (!payload) {
      this.toastService.error('Pega el QR escaneado o su identificador.');
      return;
    }

    const intentId = this.extractQrIntentId(payload);
    if (!intentId) {
      this.toastService.error('El QR no contiene una intención válida.');
      return;
    }

    this.applyScannedQrIntent(intentId, payload);
  }

  async confirmQrPayment(): Promise<void> {
    const intent = this.qrPaymentIntent();
    const sourceAccountId = this.qrPaymentSourceAccountId().trim();

    if (!intent?.id) {
      this.toastService.error('Escanea primero un QR válido.');
      return;
    }

    if (!sourceAccountId) {
      this.toastService.error('Selecciona la cuenta origen antes de pagar.');
      return;
    }

    if (intent.status !== 'PENDING') {
      this.toastService.error('Esta intención QR ya no está disponible para pago.');
      return;
    }

    this.qrPaymentConfirming.set(true);
    try {
      await this.useCase.createTransaction(
        'qr-confirm',
        {
          sourceAccountId,
          idempotencyKey: this.buildIdempotencyKey()
        },
        intent.id
      );
      this.toastService.success('Pago por QR completado con éxito.');
      this.closeQrPaymentScanner();
    } catch (error: any) {
      this.toastService.error('No se pudo completar el pago por QR: ' + (error?.message || error));
    } finally {
      this.qrPaymentConfirming.set(false);
    }
  }

  copyQrPayload(payload: string | null | undefined): void {
    if (!payload) {
      this.toastService.error('No hay payload disponible para copiar.');
      return;
    }

    navigator.clipboard.writeText(payload)
      .then(() => this.toastService.success('QR payload copiado al portapapeles.'))
      .catch(() => this.toastService.error('No se pudo copiar el QR payload.'));
  }

  ngOnDestroy(): void {
    this.stopQrPaymentScanner();
  }

  private async startQrPaymentScanner(): Promise<void> {
    if (!this.qrPaymentScannerOpen()) {
      return;
    }

    const videoElement = this.qrPaymentVideo?.nativeElement;
    if (!videoElement) {
      window.setTimeout(() => void this.startQrPaymentScanner(), 50);
      return;
    }

    this.qrPaymentScannerLoading.set(true);
    this.qrPaymentScannerError.set(null);

    try {
      const controls = await this.qrReader.decodeFromVideoDevice(undefined, videoElement, (result, error, scannerControls) => {
        if (result) {
          scannerControls?.stop();
          this.qrScannerControls = null;
          videoElement.pause();
          void this.handleQrPaymentScan(result.getText());
          return;
        }

        if (error && (error as { name?: string }).name !== 'NotFoundException') {
          this.qrPaymentScannerError.set(this.describeScannerError(error));
        }
      });
      this.qrScannerControls = controls;
      this.qrPaymentScannerLoading.set(false);
    } catch (error: any) {
      this.qrPaymentScannerLoading.set(false);
      this.qrPaymentScannerError.set(this.describeScannerError(error));
      this.toastService.error('No se pudo abrir la cámara para leer el QR.');
    } finally {
      this.qrPaymentScannerLoading.set(false);
    }
  }

  private async generateQrCode(payload: string | null | undefined): Promise<void> {
    if (!payload) {
      this.qrIntentQrDataUrl.set(null);
      return;
    }

    this.qrIntentQrLoading.set(true);
    try {
      const dataUrl = await QRCode.toDataURL(payload, {
        errorCorrectionLevel: 'M',
        margin: 1,
        width: 320,
        color: {
          dark: '#1B5E20',
          light: '#FFFFFF'
        }
      });
      this.qrIntentQrDataUrl.set(dataUrl);
    } catch {
      this.qrIntentQrDataUrl.set(null);
    } finally {
      this.qrIntentQrLoading.set(false);
    }
  }

  private stopQrPaymentScanner(): void {
    try {
      this.qrScannerControls?.stop();
    } catch {
      // Ignorar fallos al detener la cámara.
    } finally {
      this.qrScannerControls = null;
    }
  }

  private resetQrPaymentState(): void {
    this.qrPaymentScannerError.set(null);
    this.qrPaymentIntent.set(null);
    this.qrPaymentManualPayloadModel = '';
    this.qrPaymentConfirming.set(false);
  }

  private async handleQrPaymentScan(payload: string): Promise<void> {
    const intentId = this.extractQrIntentId(payload);
    if (!intentId) {
      this.qrPaymentScannerError.set('El QR leído no contiene una intención válida.');
      return;
    }

    this.qrPaymentScannerLoading.set(true);
    try {
      this.applyScannedQrIntent(intentId, payload);
    } catch {
      // El error ya fue mostrado en pantalla; dejamos la cámara detenida para reintentar.
    } finally {
      this.qrPaymentScannerLoading.set(false);
    }
  }

  private applyScannedQrIntent(intentId: string, scannedPayload?: string): void {
    this.qrPaymentIntent.set({
      id: intentId,
      qrPayload: scannedPayload || `finance://pay?intentId=${intentId}`,
      status: 'PENDING'
    });
    this.qrPaymentManualPayloadModel = scannedPayload || `finance://pay?intentId=${intentId}`;
    this.stopQrPaymentScanner();
    this.qrPaymentScannerError.set(null);
  }

  private describeScannerError(error: unknown): string {
    const message = error instanceof Error ? error.message : String(error ?? '');
    if (!message) {
      return 'No se pudo acceder a la cámara.';
    }

    if (message.includes('NotAllowedError')) {
      return 'Permite el acceso a la cámara para escanear el QR.';
    }

    if (message.includes('NotFoundError')) {
      return 'No se encontró una cámara disponible en este dispositivo.';
    }

    if (message.includes('NotReadableError')) {
      return 'La cámara está en uso por otra aplicación.';
    }

    return message;
  }

  private buildIdempotencyKey(): string {
    return globalThis.crypto?.randomUUID?.() ?? `tx-${Date.now()}-${Math.random().toString(16).slice(2)}`;
  }

  private extractQrIntentId(value: unknown): string {
    if (typeof value !== 'string') {
      return '';
    }

    const raw = value.trim();
    if (!raw) {
      return '';
    }

    if (/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(raw)) {
      return raw;
    }

    try {
      const url = new URL(raw);
      const fromUrl = url.searchParams.get('intentId') || url.searchParams.get('id');
      if (fromUrl) {
        return fromUrl.trim();
      }
    } catch {
      // Fallback below.
    }

    const match = raw.match(/(?:[?&]intentId=)([^&]+)/i);
    if (match?.[1]) {
      return decodeURIComponent(match[1]).trim();
    }

    return raw;
  }

  formatMoney(value: number, currency: string): string {
    return this.currencyPipe.transform(value, currency, 'symbol', '1.2-2') ?? `${value.toFixed(2)} ${currency}`;
  }

  formatDate(value: string | null): string {
    return value ? (this.datePipe.transform(value, 'dd/MM/yyyy') ?? 'N/A') : 'N/A';
  }

  formatTime(value: string | null): string {
    return value ? (this.datePipe.transform(value, 'shortTime') ?? '') : '';
  }

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      COMPLETED: 'Completado',
      PENDING: 'Pendiente',
      PENDING_REVIEW: 'En revisión',
      PROCESSING: 'Procesando',
      AUTHORIZED: 'Autorizado',
      FAILED: 'Fallido',
      REVERSED: 'Revertido',
      PARTIALLY_REFUNDED: 'Reembolso parcial',
      CANCELLED: 'Cancelado',
      EXPIRED: 'Expirado',
    };

    return labels[status] ?? status;
  }

  typeLabel(type: string): string {
    const labels: Record<string, string> = {
      DEPOSIT: 'Depósito',
      WITHDRAWAL: 'Retiro',
      TRANSFER: 'Transferencia',
      PAYMENT: 'Pago',
      FEE: 'Comisión',
      HOLD: 'Retención',
      RELEASE: 'Liberación',
      ADJUSTMENT: 'Ajuste',
      QR_INTENT: 'Cobro por QR',
      QR_CONFIRM: 'Pago por QR'
    };

    return labels[type] ?? type;
  }

  transactionStatusClass(status: string): string {
    if (status === 'COMPLETED') {
      return 'bg-green-500/10 text-green-700';
    }
    if (status === 'PENDING' || status === 'PROCESSING' || status === 'AUTHORIZED' || status === 'PENDING_REVIEW') {
      return 'bg-amber-500/10 text-amber-700';
    }
    if (status === 'FAILED' || status === 'CANCELLED' || status === 'EXPIRED') {
      return 'bg-red-500/10 text-red-700';
    }
    return 'bg-slate-500/10 text-slate-700';
  }

  distributionWidth(total: number, max: number): number {
    return max <= 0 ? 0 : Math.max(8, Math.round((total / max) * 100));
  }

  private async ensureAccountsLoaded(): Promise<void> {
    if (this.accountListUseCase.data().length === 0) {
      await this.accountListUseCase.loadAccounts(0, 200);
    }
  }

  private buildStatusDistribution(transactions: TransactionResponse[]) {
    const map = new Map<string, number>();
    transactions.forEach((tx) => map.set(tx.status, (map.get(tx.status) ?? 0) + 1));
    const order = ['COMPLETED', 'PENDING', 'PENDING_REVIEW', 'PROCESSING', 'AUTHORIZED', 'FAILED', 'REVERSED', 'PARTIALLY_REFUNDED', 'CANCELLED', 'EXPIRED'];
    const labels: Record<string, string> = {
      COMPLETED: 'Completados',
      PENDING: 'Pendientes',
      PENDING_REVIEW: 'En revisión',
      PROCESSING: 'Procesando',
      AUTHORIZED: 'Autorizados',
      FAILED: 'Fallidos',
      REVERSED: 'Revertidos',
      PARTIALLY_REFUNDED: 'Reembolso parcial',
      CANCELLED: 'Cancelados',
      EXPIRED: 'Expirados'
    };

    return order
      .filter((code) => map.has(code))
      .map((code) => ({ code, label: labels[code] ?? code, total: map.get(code) ?? 0 }));
  }

  private buildTypeDistribution(transactions: TransactionResponse[]) {
    const map = new Map<string, number>();
    transactions.forEach((tx) => map.set(tx.type, (map.get(tx.type) ?? 0) + 1));
    const order = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT', 'FEE', 'HOLD', 'RELEASE', 'ADJUSTMENT', 'QR_INTENT', 'QR_CONFIRM'];
    const labels: Record<string, string> = {
      DEPOSIT: 'Depósitos',
      WITHDRAWAL: 'Retiros',
      TRANSFER: 'Transferencias',
      PAYMENT: 'Pagos',
      FEE: 'Comisiones',
      HOLD: 'Retenciones',
      RELEASE: 'Liberaciones',
      ADJUSTMENT: 'Ajustes',
      QR_INTENT: 'Cobros por QR',
      QR_CONFIRM: 'Pagos por QR'
    };

    return order
      .filter((code) => map.has(code))
      .map((code) => ({ code, label: labels[code] ?? code, total: map.get(code) ?? 0 }));
  }

  private buildRecentTrend(transactions: TransactionResponse[]) {
    const map = new Map<string, { total: number; amount: number; currency: string }>();

    transactions.forEach((tx) => {
      const rawDate = tx.processedAt || tx.createdAt;
      const key = rawDate ? new Date(rawDate).toLocaleDateString('es-BO', { month: 'short', day: '2-digit' }) : 'N/D';
      const current = map.get(key) ?? { total: 0, amount: 0, currency: tx.currency };
      current.total += 1;
      current.amount += Number(tx.amount || 0);
      current.currency = current.currency || tx.currency;
      map.set(key, current);
    });

    return [...map.entries()]
      .slice(-7)
      .map(([label, value]) => ({ label, total: value.total, amount: value.amount, currency: value.currency }));
  }
}
