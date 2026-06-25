import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { PublicNavbarComponent } from '../../features/landing';
import { PublicPlansUseCase } from '../../features/billing';
import { PublicBillingPlan } from '../../entities/billing';

type BillingInterval = 'MONTHLY' | 'YEARLY';

@Component({
  selector: 'app-prices-page',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule, PublicNavbarComponent],
  template: `
    <main class="min-h-screen bg-[#F3FAF1] text-[#083B16]">
      <app-public-navbar mode="prices" />

      <section class="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
        <div class="mb-10 flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
          <div class="max-w-3xl">
            <p class="text-sm font-black uppercase tracking-[0.22em] text-[#2E7D32]">Precios</p>
            <h1 class="mt-3 text-4xl font-black tracking-tight text-[#083B16] sm:text-5xl">
              Elige el plan para crear tu tenant.
            </h1>
            <p class="mt-4 text-base leading-7 text-[#49624D]">
              Puedes iniciar gratis con DEMO o crear tu tenant con pago desde el inicio usando Stripe.
            </p>
          </div>

          <div class="inline-flex w-fit rounded-full border border-[#C8E6C9] bg-white p-1 shadow-sm">
            <button
              type="button"
              (click)="billingInterval.set('MONTHLY')"
              [class.bg-[#2E7D32]]="billingInterval() === 'MONTHLY'"
              [class.text-white]="billingInterval() === 'MONTHLY'"
              class="rounded-full px-5 py-2.5 text-sm font-black text-[#2E7D32] transition-colors">
              Mensual
            </button>
            <button
              type="button"
              (click)="billingInterval.set('YEARLY')"
              [class.bg-[#2E7D32]]="billingInterval() === 'YEARLY'"
              [class.text-white]="billingInterval() === 'YEARLY'"
              class="rounded-full px-5 py-2.5 text-sm font-black text-[#2E7D32] transition-colors">
              Anual
            </button>
          </div>
        </div>

        @if (plansUseCase.status() === 'loading') {
          <div class="flex min-h-[320px] items-center justify-center rounded-[32px] border border-[#C8E6C9] bg-white shadow-sm">
            <div class="flex items-center gap-3 text-[#49624D]">
              <lucide-icon name="loader-circle" class="h-5 w-5 animate-spin"></lucide-icon>
              Cargando planes...
            </div>
          </div>
        } @else if (plansUseCase.status() === 'error') {
          <div class="rounded-[32px] border border-red-200 bg-red-50 p-6">
            <div class="flex items-start gap-3">
              <lucide-icon name="triangle-alert" class="mt-0.5 h-5 w-5 text-red-600"></lucide-icon>
              <div>
                <h2 class="font-bold text-red-800">No se pudieron cargar los planes</h2>
                <p class="mt-1 text-sm text-red-700">{{ plansUseCase.error() }}</p>
                <button
                  type="button"
                  (click)="plansUseCase.loadPlans()"
                  class="mt-4 rounded-full bg-[#2E7D32] px-4 py-2 text-sm font-black text-white">
                  Reintentar
                </button>
              </div>
            </div>
          </div>
        } @else {
          <div class="grid gap-6 lg:grid-cols-4">
            @for (plan of plans(); track plan.id) {
              <article
                class="relative flex min-h-[640px] flex-col rounded-[30px] border bg-white p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)] transition-transform hover:-translate-y-1"
                [class.border-[#2E7D32]]="isRecommended(plan)"
                [class.shadow-[0_28px_80px_rgba(46,125,50,0.20)]]="isRecommended(plan)"
                [class.ring-2]="isRecommended(plan)"
                [class.ring-[#A5D6A7]]="isRecommended(plan)"
                [class.border-[#D6E8D1]]="!isRecommended(plan)">

                @if (isRecommended(plan)) {
                  <div class="absolute -top-4 left-1/2 -translate-x-1/2 rounded-full bg-[#2E7D32] px-5 py-2 text-xs font-black uppercase tracking-[0.16em] text-white shadow-lg">
                    Recomendado
                  </div>
                }

                <div>
                  <span
                    class="inline-flex rounded-full border px-3 py-1 text-xs font-black uppercase tracking-[0.18em]"
                    [class.border-[#A5D6A7]]="isRecommended(plan)"
                    [class.bg-[#E8F5E9]]="isRecommended(plan)"
                    [class.text-[#1B5E20]]="isRecommended(plan)"
                    [class.border-[#D6E8D1]]="!isRecommended(plan)"
                    [class.text-[#6B7D6C]]="!isRecommended(plan)">
                    {{ planBadge(plan) }}
                  </span>

                  <h2 class="mt-5 text-2xl font-black text-[#083B16]">{{ displayPlanName(plan) }}</h2>

                  <p class="mt-3 min-h-[52px] text-sm leading-6 text-[#49624D]">
                    {{ plan.description || defaultDescription(plan) }}
                  </p>
                </div>

                <div class="mt-10 min-h-[92px]">
                  @if (plan.contactSales) {
                    <p class="text-[clamp(2.4rem,3vw,3.25rem)] font-black tracking-tight text-[#083B16]">
                      A medida
                    </p>
                  } @else {
                    <div class="flex min-w-0 flex-wrap items-end gap-x-2 gap-y-1">
                      <span class="whitespace-nowrap text-[clamp(2.25rem,3.1vw,3.6rem)] font-black leading-none tracking-tight text-[#083B16]">
                        {{ priceLabel(plan) }}
                      </span>
                      <span class="mb-1 whitespace-nowrap text-sm font-black text-[#49624D]">
                        {{ periodLabel(plan) }}
                      </span>
                    </div>
                  }
                </div>

                @if (plan.planType === 'DEMO') {
                  <a
                    routerLink="/onboarding"
                    [queryParams]="{ plan: 'DEMO' }"
                    class="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#2E7D32] px-4 py-3 text-sm font-black text-white transition-colors hover:bg-[#256428]">
                    Obtener gratis
                    <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
                  </a>
                } @else if (plan.contactSales) {
                  <button
                    type="button"
                    class="mt-6 inline-flex w-full cursor-not-allowed items-center justify-center gap-2 rounded-2xl bg-[#E5EAE3] px-4 py-3 text-sm font-black text-[#6B7D6C]">
                    Contactar ventas
                  </button>
                } @else {
                  <a
                    routerLink="/onboarding"
                    [queryParams]="{ plan: plan.code, billing: billingInterval() }"
                    class="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-2xl px-4 py-3 text-sm font-black transition-colors"
                    [class.bg-[#2E7D32]]="isRecommended(plan)"
                    [class.text-white]="isRecommended(plan)"
                    [class.hover:bg-[#256428]]="isRecommended(plan)"
                    [class.bg-[#083B16]]="!isRecommended(plan)"
                    [class.text-white]="!isRecommended(plan)"
                    [class.hover:bg-[#145421]]="!isRecommended(plan)">
                    Obtener {{ displayPlanName(plan) }}
                    <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
                  </a>
                }

                <div class="mt-7 border-t border-[#DDEED8] pt-6">
                  <p class="mb-4 text-sm font-black text-[#083B16]">
                    {{ featureHeader(plan) }}
                  </p>

                  <ul class="space-y-4">
                    @for (feature of features(plan); track feature) {
                      <li class="flex gap-3 text-sm leading-6 text-[#49624D]">
                        <lucide-icon name="check-circle" class="mt-0.5 h-4 w-4 shrink-0 text-[#2E7D32]"></lucide-icon>
                        <span>{{ feature }}</span>
                      </li>
                    }
                  </ul>
                </div>
              </article>
            }
          </div>
        }
      </section>
    </main>
  `
})
export class PricesPageComponent implements OnInit {
  readonly plansUseCase = inject(PublicPlansUseCase);
  readonly billingInterval = signal<BillingInterval>('MONTHLY');

  readonly plans = computed(() => this.plansUseCase.plans());

  ngOnInit(): void {
    this.plansUseCase.loadPlans();
  }

  displayPlanName(plan: PublicBillingPlan): string {
    const code = plan.code?.toUpperCase();

    if (code === 'DEMO') return 'Free';
    if (code === 'PRO') return 'Professional';

    return plan.name;
  }

  planBadge(plan: PublicBillingPlan): string {
    if (plan.planType === 'DEMO') return 'Plan gratis';
    if (plan.contactSales) return 'Enterprise';
    return 'Paid';
  }

  priceLabel(plan: PublicBillingPlan): string {
    if (plan.planType === 'DEMO') {
      return `${plan.currency || 'USD'} 0`;
    }

    const amount = this.billingInterval() === 'MONTHLY'
      ? plan.monthlyAmount
      : plan.yearlyAmount;

    if (amount == null) {
      return 'A medida';
    }

    return `${plan.currency || 'USD'} ${this.formatMoney(amount)}`;
  }

  periodLabel(plan: PublicBillingPlan): string {
    if (plan.planType === 'DEMO') return '/demo';
    if (plan.contactSales) return '';
    return this.billingInterval() === 'MONTHLY' ? '/mes' : '/año';
  }

  features(plan: PublicBillingPlan): string[] {
    if (plan.planType === 'DEMO') {
      return [
        `Hasta ${plan.maxUsers} usuarios`,
        `Hasta ${plan.maxRoles} roles personalizados`,
        `${plan.trialDays ?? 10} días de prueba`,
        'Ideal para validar el flujo completo'
      ];
    }

    if (plan.contactSales) {
      return [
        'Capacidad personalizada',
        'Condiciones comerciales especiales',
        'Acompañamiento comercial',
        'No usa checkout automático'
      ];
    }

    return [
      `Hasta ${plan.maxUsers} usuarios`,
      `Hasta ${plan.maxRoles} roles personalizados`,
      'Pago seguro con Stripe',
      'Puedes cambiar de plan desde tu sesión'
    ];
  }

  featureHeader(plan: PublicBillingPlan): string {
    if (plan.planType === 'DEMO') return 'Incluye:';
    if (plan.contactSales) return 'Para empresas que necesitan:';
    return 'Todo lo necesario para operar:';
  }

  defaultDescription(plan: PublicBillingPlan): string {
    if (plan.planType === 'DEMO') return 'Prueba la plataforma sin costo antes de pasar a un plan pagado.';
    if (plan.contactSales) return 'Plan personalizado para empresas grandes.';
    return 'Plan de suscripción para operar tu tenant.';
  }

  isRecommended(plan: PublicBillingPlan): boolean {
    return plan.code?.toUpperCase() === 'PRO';
  }

  private formatMoney(value: number): string {
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: value % 1 === 0 ? 0 : 2,
      maximumFractionDigits: 2
    }).format(value);
  }
}
