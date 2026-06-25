import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { LandingFeatureCard } from '../../model/landing-plan.model';

@Component({
  selector: 'app-landing-features',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <section class="bg-[#F7FBF5] px-4 py-20 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-7xl">
        <div class="max-w-3xl">
          <p class="text-sm font-black uppercase tracking-[0.22em] text-[#2E7D32]">Beneficios</p>
          <h2 class="mt-3 text-3xl font-black tracking-tight text-[#083B16]">
            El sistema completo en módulos claros y consistentes.
          </h2>
          <p class="mt-3 text-base leading-7 text-[#49624D]">
            La idea es que la landing no mezcle demasiadas acciones: primero el hero, luego las tarjetas de valor y después los módulos principales.
          </p>
        </div>

        <div class="mt-10 grid gap-6 md:grid-cols-2">
          @for (feature of features; track feature.title) {
            <article class="rounded-[28px] border border-[#D6E8D1] bg-white p-7 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
              <div class="mb-6 inline-flex rounded-2xl bg-[#E8F5E9] p-4 text-[#2E7D32]">
                <lucide-icon [name]="feature.icon" class="h-6 w-6"></lucide-icon>
              </div>
              <h3 class="text-xl font-black text-[#083B16]">{{ feature.title }}</h3>
              <p class="mt-3 text-sm leading-7 text-[#49624D]">{{ feature.description }}</p>
            </article>
          }
        </div>
      </div>
    </section>
  `
})
export class LandingFeaturesComponent {
  @Input({ required: true }) features: LandingFeatureCard[] = [];
}
