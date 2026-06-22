import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { ServiceBillPaymentResponse, ServicePaymentResponse } from '../../../../entities/service-payments';

type PaymentDetail = ServiceBillPaymentResponse | ServicePaymentResponse | null;

@Component({
  selector: 'app-service-payment-detail',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <div *ngIf="isOpen" class="app-modal-overlay" (click)="close.emit()">
      <div class="app-modal-panel app-modal-panel-sm" (click)="$event.stopPropagation()">
        <div class="app-modal-header border-b border-[#E8F2E2] pb-5">
          <div>
            <h2 class="app-modal-title">Comprobante de pago</h2>
            <p class="app-modal-subtitle">
              Detalle del pago de servicio registrado.
            </p>
          </div>

          <button
            type="button"
            (click)="close.emit()"
            class="cursor-pointer rounded-full border border-[#DDEED8] p-2 text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
            <lucide-icon name="x" [size]="20"></lucide-icon>
          </button>
        </div>

        <div *ngIf="payment" class="space-y-5 pt-6">
          <div class="rounded-[24px] border border-[#C8E6C9] bg-[#FAFCF8] p-5 text-center">
            <p class="text-xs font-bold uppercase tracking-wide text-[#567157]">Recibo</p>
            <p class="mt-2 text-2xl font-black text-[#1B5E20]">{{ receiptNumber }}</p>
            <p class="mt-1 text-sm text-[#6B7D6C]">{{ amount | number:'1.2-2' }} {{ currency }}</p>
          </div>

          <div class="grid gap-3 text-sm">
            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Proveedor</span>
              <span class="font-semibold text-[#1B5E20]">{{ providerName }}</span>
            </div>

            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Código</span>
              <span class="font-semibold text-[#1B5E20]">{{ serviceCustomerCode }}</span>
            </div>

            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Titular</span>
              <span class="font-semibold text-[#1B5E20]">{{ serviceCustomerName }}</span>
            </div>

            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Periodo</span>
              <span class="font-semibold text-[#1B5E20]">{{ billingPeriod }}</span>
            </div>

            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Cuenta</span>
              <span class="font-semibold text-[#1B5E20]">{{ sourceAccountNumber }}</span>
            </div>

            <div class="flex justify-between gap-4 border-b border-[#E8F2E2] pb-2">
              <span class="text-[#6B7D6C]">Transacción</span>
              <span class="font-semibold text-[#1B5E20]">{{ transactionId }}</span>
            </div>

            <div class="flex justify-between gap-4">
              <span class="text-[#6B7D6C]">Fecha</span>
              <span class="font-semibold text-[#1B5E20]">{{ paidAt | date:'medium' }}</span>
            </div>
          </div>

          <div class="app-modal-footer border-t border-[#E8F2E2] pt-5">
            <button
              type="button"
              (click)="close.emit()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ServicePaymentDetailComponent {
  @Input() isOpen = false;
  @Input() payment: PaymentDetail = null;
  @Output() close = new EventEmitter<void>();

  get receiptNumber(): string {
    return this.payment ? (this.payment as any).receiptNumber : '';
  }

  get providerName(): string {
    if (!this.payment) return '';
    return (this.payment as any).providerName ?? (this.payment as any).provider?.name ?? '';
  }

  get serviceCustomerCode(): string {
    return this.payment ? (this.payment as any).serviceCustomerCode : '';
  }

  get serviceCustomerName(): string {
    return this.payment ? ((this.payment as any).serviceCustomerName ?? '') : '';
  }

  get billingPeriod(): string {
    return this.payment ? (this.payment as any).billingPeriod : '';
  }

  get amount(): number {
    return this.payment ? Number((this.payment as any).amount ?? 0) : 0;
  }

  get currency(): string {
    return this.payment ? (this.payment as any).currency : '';
  }

  get sourceAccountNumber(): string {
    return this.payment
      ? ((this.payment as any).sourceAccountNumber ?? (this.payment as any).paidByAccountNumber ?? '-')
      : '-';
  }

  get transactionId(): string {
    return this.payment
      ? ((this.payment as any).transactionId ?? (this.payment as any).paidTransactionId ?? '-')
      : '-';
  }

  get paidAt(): string {
    return this.payment ? (this.payment as any).paidAt : '';
  }
}
