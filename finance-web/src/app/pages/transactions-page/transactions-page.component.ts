import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule, Plus, ChevronDown, RefreshCcw, MoreHorizontal, Eye, RotateCcw, Reply, X, CheckCircle, XCircle, AlertTriangle, ArrowDownToLine, ArrowUpFromLine, ArrowRightLeft, Send } from 'lucide-angular';
import QRCode from 'qrcode';
import { HasPermissionPipe } from '../../shared/api';
import { ToastService } from '../../shared/ui/toast/toast.service';
import { PermissionService } from '../../shared/lib/auth/permission.service';
import { AccountListUseCase } from '../../features/account-management';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { TransactionSlideOverComponent, TransactionActionType, TransactionsListUseCase } from '../../features/transactions-management';
import { QrTransactionIntentResponse, TransactionResponse } from '../../entities/transactions';

type TransactionStatusFilter = 'ALL' | 'COMPLETED' | 'PENDING' | 'FAILED' | 'REVERSED';
type TransactionTypeFilter = 'ALL' | 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER' | 'PAYMENT';

@Component({
  selector: 'app-transactions-page',
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
              Owner Admin
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Transacciones
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Visualiza y gestiona el flujo transaccional del tenant con una vista clara, rápida y lista para operar.
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
                    Operaciones básicas
                  </div>
                  <button *ngIf="'transactions.create.deposit' | hasPermission" type="button" (click)="openSlideOver('deposit')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-down-to-line" [size]="16"></lucide-icon>
                    Depósito
                  </button>
                  <button *ngIf="'transactions.create.withdrawal' | hasPermission" type="button" (click)="openSlideOver('withdrawal')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-up-from-line" [size]="16"></lucide-icon>
                    Retiro
                  </button>
                  <button *ngIf="'transactions.create.transfer' | hasPermission" type="button" (click)="openSlideOver('transfer')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-right-left" [size]="16"></lucide-icon>
                    Transferencia
                  </button>
                  <button *ngIf="'transactions.create.payment' | hasPermission" type="button" (click)="openSlideOver('payment')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="send" [size]="16"></lucide-icon>
                    Pago
                  </button>
                  <div class="border-y border-[#EEF5EA] px-4 py-2 text-[11px] font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">
                    Operaciones avanzadas
                  </div>
                  <button *ngIf="'transactions.fee' | hasPermission" type="button" (click)="openSlideOver('fee')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="credit-card" [size]="16"></lucide-icon>
                    Comisión
                  </button>
                  <button *ngIf="'transactions.hold' | hasPermission" type="button" (click)="openSlideOver('hold')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="alert-triangle" [size]="16"></lucide-icon>
                    Retención
                  </button>
                  <button *ngIf="'transactions.release' | hasPermission" type="button" (click)="openSlideOver('release')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="rotate-ccw" [size]="16"></lucide-icon>
                    Liberación
                  </button>
                  <button *ngIf="'transactions.adjust' | hasPermission" type="button" (click)="openSlideOver('adjustment')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="arrow-right-left" [size]="16"></lucide-icon>
                    Ajuste
                  </button>
                  <button *ngIf="'transactions.qr.create' | hasPermission" type="button" (click)="openSlideOver('qr-intent')" class="flex w-full cursor-pointer items-center gap-3 px-4 py-3 text-left text-sm font-medium text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                    <lucide-icon name="send" [size]="16"></lucide-icon>
                    Cobro por QR
                  </button>
                </div>
              }
            </div>
          </div>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">En página</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().total }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Registros visibles en la página actual</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Completadas</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().completed }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Operaciones ya procesadas</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Pendientes</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().pending }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">En cola o revisión</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Fallidas</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ transactionStats().failed }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Con error o rechazo</p>
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
              <option value="COMPLETED">Completadas</option>
              <option value="PENDING">Pendientes</option>
              <option value="FAILED">Fallidas</option>
              <option value="REVERSED">Revertidas</option>
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

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar transacciones</h3>
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
          <p>Cargando transacciones...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        @if (useCase.page(); as page) {
          <div class="flex flex-col gap-3 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#567157] sm:flex-row sm:items-center sm:justify-between">
            <p>
              <span class="font-semibold text-[#1B5E20]">{{ page.totalElements }}</span>
              registro(s) en el total paginado
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
                      @if (tx.failureReason) {
                        <div class="mt-1 max-w-[140px] truncate text-[10px] text-red-600" [title]="tx.failureReason">{{ tx.failureReason }}</div>
                      }
                    </td>
                    <td class="px-6 py-4 text-right">
                      <div class="text-sm text-[#1B5E20]">{{ formatDate(tx.processedAt || tx.createdAt) }}</div>
                      <div class="text-xs text-[#6B7D6C]">{{ formatTime(tx.processedAt || tx.createdAt) }}</div>
                    </td>
                    <td class="px-6 py-4 text-center">
                      @if (canViewTransactionActions) {
                        <div class="relative group inline-block text-left" (click)="$event.stopPropagation()">
                          <button type="button" class="cursor-pointer rounded-md p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
                            <lucide-icon name="more-horizontal" [size]="16"></lucide-icon>
                          </button>
                          <div class="absolute right-0 mt-1 w-48 overflow-hidden rounded-2xl border border-[#DDEED8] bg-white shadow-xl opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50">
                            <div class="py-1">
                              @if (canViewTransactionDetail) {
                                <button type="button" (click)="openDetail(tx)" class="flex w-full cursor-pointer items-center gap-2 px-4 py-2.5 text-left text-xs font-medium text-[#1B5E20] hover:bg-[#F7FBF3]">
                                  <lucide-icon name="eye" [size]="14"></lucide-icon>
                                  Ver detalle
                                </button>
                              }
                              <button *ngIf="('transactions.reverse' | hasPermission) && tx.status === 'COMPLETED'" type="button" (click)="openActionModal('reverse', tx)" class="flex w-full cursor-pointer items-center gap-2 px-4 py-2.5 text-left text-xs font-medium text-orange-600 hover:bg-orange-50">
                                <lucide-icon name="rotate-ccw" [size]="14"></lucide-icon>
                                Revertir
                              </button>
                              <button *ngIf="('transactions.refund' | hasPermission) && tx.status === 'COMPLETED'" type="button" (click)="openActionModal('refund', tx)" class="flex w-full cursor-pointer items-center gap-2 px-4 py-2.5 text-left text-xs font-medium text-blue-600 hover:bg-blue-50">
                                <lucide-icon name="reply" [size]="14"></lucide-icon>
                                Reembolsar
                              </button>
                            </div>
                          </div>
                        </div>
                      }
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="px-6 py-12 text-center text-[#6B7D6C]">
                      No hay transacciones para mostrar en esta página.
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

                @if (canViewTransactionActions) {
                  <div class="mt-3 flex items-center justify-between gap-2" (click)="$event.stopPropagation()">
                    @if (canViewTransactionDetail) {
                      <button type="button" (click)="openDetail(tx)" class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#DDEED8] bg-white px-3 py-2 text-xs font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                        <lucide-icon name="eye" [size]="14"></lucide-icon>
                        Ver detalle
                      </button>
                    }
                    <div class="flex items-center gap-2">
                      <button *ngIf="('transactions.reverse' | hasPermission) && tx.status === 'COMPLETED'" type="button" (click)="openActionModal('reverse', tx)" class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-orange-200 bg-orange-50 px-3 py-2 text-xs font-semibold text-orange-700 transition-colors hover:bg-orange-100">
                        <lucide-icon name="rotate-ccw" [size]="14"></lucide-icon>
                        Revertir
                      </button>
                      <button *ngIf="('transactions.refund' | hasPermission) && tx.status === 'COMPLETED'" type="button" (click)="openActionModal('refund', tx)" class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-blue-200 bg-blue-50 px-3 py-2 text-xs font-semibold text-blue-700 transition-colors hover:bg-blue-100">
                        <lucide-icon name="reply" [size]="14"></lucide-icon>
                        Reembolsar
                      </button>
                    </div>
                  </div>
                }
              </article>
            } @empty {
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-8 text-center text-sm text-[#6B7D6C]">
                No hay transacciones para mostrar en esta página.
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
              <h3 class="mt-1 text-2xl font-black text-[#1B5E20]">Comparte el QR para que lo escaneen y paguen</h3>
              <p class="mt-1 text-sm text-[#5F6F5F]">La intención quedó registrada y el código QR está listo para cobrarse desde la cámara del otro usuario.</p>
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
                  <div class="text-center text-sm text-[#6B7D6C]">
                    No se pudo generar el QR.
                  </div>
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
              <pre class="mt-3 max-h-64 overflow-auto rounded-2xl bg-[#F7FBF3] p-4 text-xs leading-5 text-[#355A38]">{{ qrIntent.qrPayload }}</pre>
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
                <p class="text-xs font-semibold uppercase tracking-[0.16em] text-[#6B7D6C]">Detalle de transacción</p>
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
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ detail.sourceAccountId || 'Sin dato' }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Destino</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ detail.targetAccountDisplayName || detail.targetAccountNumber || 'N/A' }}</p>
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ detail.targetAccountId || 'Sin dato' }}</p>
              </div>
              <div class="rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] p-4">
                <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">Fechas</p>
                <p class="mt-2 text-sm font-semibold text-[#1B5E20]">{{ formatDate(detail.processedAt || detail.createdAt) }}</p>
                <p class="mt-1 text-xs text-[#6B7D6C]">{{ formatTime(detail.processedAt || detail.createdAt) }}</p>
              </div>
            </div>

            <div class="grid gap-4 lg:grid-cols-2">
              <div class="rounded-2xl border border-[#DDEED8] bg-white p-5">
                <h4 class="text-sm font-bold uppercase tracking-[0.14em] text-[#6B7D6C]">Datos operativos</h4>
                <div class="mt-4 grid gap-3 sm:grid-cols-2">
                  <div><p class="text-xs text-[#6B7D6C]">Referencia externa</p><p class="font-medium text-[#1B5E20]">{{ detail.externalReference || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Idempotency key</p><p class="font-medium text-[#1B5E20] break-all">{{ detail.idempotencyKey || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Requested by</p><p class="font-medium text-[#1B5E20] break-all">{{ detail.requestedByUserId || 'N/A' }}</p></div>
                  <div><p class="text-xs text-[#6B7D6C]">Approved by</p><p class="font-medium text-[#1B5E20] break-all">{{ detail.approvedByUserId || 'N/A' }}</p></div>
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
                  <p class="mt-4 text-sm text-[#6B7D6C]">No hay detalle de FX para esta transacción.</p>
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

            <div class="flex flex-col-reverse gap-3 border-t border-[#EEF5EA] pt-5 sm:flex-row sm:justify-end">
              <button type="button" (click)="closeDetail()" class="inline-flex cursor-pointer items-center justify-center rounded-full border border-[#DDEED8] bg-white px-4 py-2 text-sm font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
                Cerrar
              </button>
              <button *ngIf="('transactions.reverse' | hasPermission) && detail.status === 'COMPLETED'" type="button" (click)="openActionModal('reverse', detail)" class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full border border-orange-200 bg-orange-50 px-4 py-2 text-sm font-semibold text-orange-700 transition-colors hover:bg-orange-100">
                <lucide-icon name="rotate-ccw" [size]="16"></lucide-icon>
                Revertir
              </button>
              <button *ngIf="('transactions.refund' | hasPermission) && detail.status === 'COMPLETED'" type="button" (click)="openActionModal('refund', detail)" class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                <lucide-icon name="reply" [size]="16"></lucide-icon>
                Reembolsar
              </button>
            </div>
          </div>
        </div>
      </div>
    }

    <div *ngIf="actionModalOpen()" class="fixed inset-0 z-[110] flex items-start justify-center p-4 sm:items-center">
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm transition-opacity" (click)="closeActionModal()"></div>
      <div class="relative w-full max-w-md max-h-[calc(100vh-2rem)] overflow-y-auto overscroll-contain rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.12)]">
        <button type="button" (click)="closeActionModal()" class="absolute right-4 top-4 cursor-pointer rounded-full p-2 text-[#6B7D6C] transition-colors hover:bg-[#F1F8E9] hover:text-[#1B5E20]">
          <lucide-icon name="x" [size]="20"></lucide-icon>
        </button>

        <div class="flex flex-col items-center text-center mt-2">
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full" [ngClass]="actionModalType === 'reverse' ? 'bg-orange-500/10 text-orange-600' : 'bg-blue-500/10 text-blue-600'">
            <lucide-icon [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'reply'" [size]="32"></lucide-icon>
          </div>
          <h3 class="text-xl font-black text-[#1B5E20]">
            {{ actionModalType === 'reverse' ? 'Revertir transacción' : 'Reembolsar transacción' }}
          </h3>
          <p class="mt-1 text-sm text-[#6B7D6C]">
            Confirma la operación con un motivo claro.
          </p>
        </div>

        <div class="mt-6 space-y-4">
          <div class="space-y-2">
            <label class="text-sm font-semibold text-[#1B5E20]">Motivo</label>
            <textarea
              [(ngModel)]="actionReason"
              rows="3"
              placeholder="Describe por qué se realizará la operación"
              class="flex w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 py-3 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white"></textarea>
          </div>

          <div *ngIf="actionModalType === 'refund'" class="space-y-2">
            <label class="text-sm font-semibold text-[#1B5E20]">Monto</label>
            <input
              type="number"
              [(ngModel)]="actionAmount"
              min="0.01"
              step="0.01"
              class="flex h-11 w-full rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none transition focus:border-[#A5D6A7] focus:bg-white" />
            <p class="text-xs text-[#6B7D6C]">Original: {{ selectedTransactionForAction?.amount | currency:selectedTransactionForAction?.currency }}</p>
          </div>

          @if (actionError) {
            <div class="rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
              {{ actionError }}
            </div>
          }
        </div>

        <div class="mt-8 flex gap-3">
          <button type="button" (click)="closeActionModal()" class="flex-1 inline-flex cursor-pointer items-center justify-center rounded-full border border-[#DDEED8] bg-white px-4 py-2.5 text-sm font-semibold text-[#1B5E20] transition-colors hover:bg-[#F7FBF3]">
            Cancelar
          </button>
          <button
            type="button"
            (click)="confirmAction()"
            [disabled]="!actionReason || (actionModalType === 'refund' && (!actionAmount || actionAmount <= 0)) || isProcessingAction"
            class="flex-1 inline-flex cursor-pointer items-center justify-center rounded-full px-4 py-2.5 text-sm font-semibold text-white transition-colors disabled:opacity-50"
            [ngClass]="actionModalType === 'reverse' ? 'bg-orange-600 hover:bg-orange-700' : 'bg-[#2E7D32] hover:bg-[#256428]'">
            <lucide-icon *ngIf="!isProcessingAction" [name]="actionModalType === 'reverse' ? 'rotate-ccw' : 'reply'" [size]="16" class="mr-2"></lucide-icon>
            <svg *ngIf="isProcessingAction" class="mr-2 h-4 w-4 animate-spin" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ actionModalType === 'reverse' ? 'Confirmar reversión' : 'Confirmar reembolso' }}
          </button>
        </div>
      </div>
    </div>
  `
})
export class TransactionsPageComponent implements OnInit {
  public readonly useCase = inject(TransactionsListUseCase);
  public readonly accountListUseCase = inject(AccountListUseCase);
  private readonly toastService = inject(ToastService);
  private readonly datePipe = inject(DatePipe);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly permissionService = inject(PermissionService);

  readonly pageSize = 20;
  readonly isSlideOverOpen = signal(false);
  readonly selectedTransactionType = signal<TransactionActionType>('deposit');
  readonly selectedTransaction = signal<TransactionResponse | null>(null);
  readonly createMenuOpen = signal(false);
  readonly actionModalOpen = signal(false);
  readonly qrIntentModalOpen = signal(false);
  readonly qrIntentResult = signal<QrTransactionIntentResponse | null>(null);
  readonly qrIntentQrDataUrl = signal<string | null>(null);
  readonly qrIntentQrLoading = signal(false);
  readonly searchTerm = signal('');
  readonly statusFilter = signal<TransactionStatusFilter>('ALL');
  readonly typeFilter = signal<TransactionTypeFilter>('ALL');
  searchTermModel = '';
  statusFilterModel: TransactionStatusFilter = 'ALL';
  typeFilterModel: TransactionTypeFilter = 'ALL';

  selectedTransactionForAction: TransactionResponse | null = null;
  actionModalType: 'reverse' | 'refund' | null = null;
  actionReason = '';
  actionAmount: number | null = null;
  actionError: string | null = null;
  isProcessingAction = false;

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
    const failed = txs.filter((tx) => ['FAILED', 'REJECTED', 'CANCELLED', 'EXPIRED'].includes(tx.status)).length;
    const reversed = txs.filter((tx) => ['REVERSED', 'PARTIALLY_REFUNDED'].includes(tx.status)).length;
    const totalAmount = txs.reduce((sum, tx) => sum + Number(tx.amount || 0), 0);
    const currency = txs.find((tx) => tx.currency)?.currency ?? 'USD';

    return { total: txs.length, completed, pending, failed, reversed, totalAmount, currency };
  });

  readonly statusDistribution = computed(() => this.buildStatusDistribution(this.pageTransactions()));
  readonly typeDistribution = computed(() => this.buildTypeDistribution(this.pageTransactions()));
  readonly recentTrend = computed(() => this.buildRecentTrend(this.pageTransactions()));
  readonly statusDistributionMax = computed(() => Math.max(1, ...this.statusDistribution().map((item) => item.total)));
  readonly typeDistributionMax = computed(() => Math.max(1, ...this.typeDistribution().map((item) => item.total)));
  readonly recentTrendMax = computed(() => Math.max(1, ...this.recentTrend().map((item) => item.total)));

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
      'transactions.create.deposit',
      'transactions.create.withdrawal',
      'transactions.create.transfer',
      'transactions.create.payment',
      'transactions.fee',
      'transactions.hold',
      'transactions.release',
      'transactions.adjust',
      'transactions.qr.create'
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
      } else {
        await this.useCase.executeTransaction(event.type, event.request, event.referenceId);
        this.toastService.success('Transacción procesada con éxito.');
      }
      this.isSlideOverOpen.set(false);
    } catch (error: any) {
      this.toastService.error('Error al procesar la transacción: ' + (error?.message || error));
    }
  }

  get canViewTransactionDetail(): boolean {
    return this.permissionService.hasAnyPermission('transactions.detail', 'transactions.admin.read');
  }

  get canViewTransactionActions(): boolean {
    return this.canViewTransactionDetail || this.permissionService.hasAnyPermission('transactions.reverse', 'transactions.refund');
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

  openActionModal(type: 'reverse' | 'refund', transaction: TransactionResponse): void {
    this.actionModalType = type;
    this.selectedTransactionForAction = transaction;
    this.actionReason = '';
    this.actionAmount = transaction.amount;
    this.actionError = null;
    this.isProcessingAction = false;
    this.actionModalOpen.set(true);
    this.selectedTransaction.set(null);
  }

  closeActionModal(): void {
    this.actionModalOpen.set(false);
    this.selectedTransactionForAction = null;
    this.actionModalType = null;
  }

  closeQrIntentModal(): void {
    this.qrIntentModalOpen.set(false);
    this.qrIntentResult.set(null);
    this.qrIntentQrDataUrl.set(null);
    this.qrIntentQrLoading.set(false);
  }

  async copyQrPayload(payload: string): Promise<void> {
    try {
      await navigator.clipboard.writeText(payload);
      this.toastService.success('QR payload copiado al portapapeles.');
    } catch {
      this.toastService.error('No se pudo copiar el QR payload.');
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

  async confirmAction(): Promise<void> {
    if (!this.selectedTransactionForAction || !this.actionReason || !this.actionModalType) {
      return;
    }

    if (this.actionModalType === 'refund' && (!this.actionAmount || this.actionAmount <= 0)) {
      return;
    }

    this.isProcessingAction = true;
    this.actionError = null;

    try {
      if (this.actionModalType === 'reverse') {
        await this.useCase.reverseTransaction(this.selectedTransactionForAction.id, this.actionReason);
      } else {
        await this.useCase.refundTransaction(this.selectedTransactionForAction.id, this.actionReason, this.actionAmount!);
      }
      this.toastService.success(this.actionModalType === 'reverse' ? 'Transacción revertida.' : 'Transacción reembolsada.');
      this.closeActionModal();
    } catch (error: any) {
      this.actionError = error.message || 'Error al procesar la operación';
    } finally {
      this.isProcessingAction = false;
    }
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
      COMPLETED: 'Completada',
      PENDING: 'Pendiente',
      PENDING_REVIEW: 'En revisión',
      PROCESSING: 'Procesando',
      AUTHORIZED: 'Autorizada',
      FAILED: 'Fallida',
      REVERSED: 'Revertida',
      PARTIALLY_REFUNDED: 'Reembolso parcial',
      CANCELLED: 'Cancelada',
      EXPIRED: 'Expirada',
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
      COMPLETED: 'Completadas',
      PENDING: 'Pendientes',
      PENDING_REVIEW: 'En revisión',
      PROCESSING: 'Procesando',
      AUTHORIZED: 'Autorizadas',
      FAILED: 'Fallidas',
      REVERSED: 'Revertidas',
      PARTIALLY_REFUNDED: 'Reembolso parcial',
      CANCELLED: 'Canceladas',
      EXPIRED: 'Expiradas'
    };

    return order
      .filter((code) => map.has(code))
      .map((code) => ({ code, label: labels[code] ?? code, total: map.get(code) ?? 0 }));
  }

  private buildTypeDistribution(transactions: TransactionResponse[]) {
    const map = new Map<string, number>();
    transactions.forEach((tx) => map.set(tx.type, (map.get(tx.type) ?? 0) + 1));
    const order = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT', 'HOLD', 'RELEASE', 'ADJUSTMENT', 'FEE', 'QR_INTENT', 'QR_CONFIRM'];
    const labels: Record<string, string> = {
      DEPOSIT: 'Depósitos',
      WITHDRAWAL: 'Retiros',
      TRANSFER: 'Transferencias',
      PAYMENT: 'Pagos',
      HOLD: 'Retenciones',
      RELEASE: 'Liberaciones',
      ADJUSTMENT: 'Ajustes',
      FEE: 'Comisiones',
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
