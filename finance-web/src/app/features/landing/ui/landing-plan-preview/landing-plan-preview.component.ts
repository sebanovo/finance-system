import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { LandingPlanPreview } from '../../model/landing-plan.model';

@Component({
  selector: 'app-landing-plan-preview',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <section class="bg-[#F7FBF5] px-4 py-20 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-7xl">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="max-w-3xl">
            <p class="text-sm font-black uppercase tracking-[0.22em] text-[#2E7D32]">Planes</p>
            <h2 class="mt-3 text-3xl font-black tracking-tight text-[#083B16]">
              Empieza gratis o elige un plan con más capacidad.
            </h2>
            <p class="mt-3 text-base leading-7 text-[#49624D]">
              La landing solo presenta el valor; el detalle completo vive en la página de precios.
            </p>
          </div>

          <a routerLink="/prices"
            class="inline-flex w-fit items-center justify-center gap-2 rounded-2xl border border-[#C8E6C9] bg-white px-5 py-3 text-sm font-black text-[#2E7D32] shadow-sm transition-colors hover:bg-[#F1F8E9]">
            Ver planes completos
            <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
          </a>
        </div>

        <div class="mt-10 grid gap-6 md:grid-cols-2 xl:grid-cols-4">
          @for (plan of plans; track plan.code) {
            <article class="flex h-full flex-col rounded-[28px] border border-[#D6E8D1] bg-white p-7 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <span class="inline-flex rounded-full bg-[#E8F5E9] px-3 py-1 text-xs font-black uppercase tracking-[0.2em] text-[#2E7D32]">
                    {{ plan.badge || plan.code }}
                  </span>
                  <h3 class="mt-4 text-2xl font-black text-[#083B16]">{{ plan.name }}</h3>
                  <p class="mt-2 text-sm leading-7 text-[#49624D]">{{ plan.description }}</p>
                </div>
              </div>

              <p class="mt-8 text-4xl font-black tracking-tight text-[#083B16]">{{ plan.priceLabel }}</p>

              <a [routerLink]="plan.ctaLink"
                [queryParams]="plan.code === 'DEMO' ? { plan: 'DEMO' } : null"
                class="mt-8 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-[#2E7D32] px-5 py-3 text-sm font-black text-white transition-colors hover:bg-[#256428]">
                {{ plan.ctaLabel }}
                <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
              </a>
            </article>
          }
        </div>
      </div>
    </section>
  `
})
export class LandingPlanPreviewComponent {
  @Input({ required: true }) plans: LandingPlanPreview[] = [];
}
