import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { LandingFeatureItem } from '../../model/landing-plan.model';

@Component({
  selector: 'app-landing-quick-cards',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <section class="bg-[#F3FAF1] px-4 pb-20 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-7xl">
        <div class="mb-8 max-w-2xl">
          <p class="text-sm font-black uppercase tracking-[0.22em] text-[#2E7D32]">Funciones destacadas</p>
          <h2 class="mt-3 text-3xl font-black tracking-tight text-[#083B16]">
            Todo lo esencial separado del hero.
          </h2>
          <p class="mt-3 text-base leading-7 text-[#49624D]">
            Mantén la parte superior enfocada en la acción principal y deja estos beneficios como apoyo visual.
          </p>
        </div>

        <div class="grid gap-6 md:grid-cols-3">
          @for (card of cards; track card.title) {
            <article class="rounded-[28px] border border-[#D6E8D1] bg-white p-7 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
              <div class="mb-7 flex h-14 w-14 items-center justify-center rounded-2xl bg-[#E8F5E9] text-[#2E7D32]">
                <lucide-icon [name]="card.icon" class="h-6 w-6"></lucide-icon>
              </div>

              <h3 class="text-lg font-black text-[#083B16]">{{ card.title }}</h3>
              <p class="mt-3 text-sm leading-7 text-[#49624D]">{{ card.description }}</p>
            </article>
          }
        </div>
      </div>
    </section>
  `
})
export class LandingQuickCardsComponent {
  @Input({ required: true }) cards: LandingFeatureItem[] = [];
}
