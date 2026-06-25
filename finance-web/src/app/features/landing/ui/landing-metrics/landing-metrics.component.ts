import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LandingMetricCard } from '../../model/landing-plan.model';

@Component({
  selector: 'app-landing-metrics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="bg-[#F7FBF5] px-4 pb-20 sm:px-6 lg:px-8">
      <div class="mx-auto grid max-w-7xl gap-4 sm:grid-cols-3">
        @for (metric of metrics; track metric.label) {
          <article class="rounded-[24px] border border-[#D6E8D1] bg-white p-6 shadow-[0_10px_30px_rgba(27,94,32,0.06)]">
            <p class="text-3xl font-black text-[#083B16]">{{ metric.value }}</p>
            <p class="mt-1 text-sm leading-6 text-[#49624D]">{{ metric.label }}</p>
          </article>
        }
      </div>
    </section>
  `
})
export class LandingMetricsComponent {
  @Input({ required: true }) metrics: LandingMetricCard[] = [];
}
