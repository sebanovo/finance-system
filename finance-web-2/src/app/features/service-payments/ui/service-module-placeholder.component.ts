import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

export interface ServiceModuleShortcut {
  label: string;
  route: string;
  icon: string;
  description: string;
}

@Component({
  selector: 'app-service-module-placeholder',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
      <div class="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
        <div class="space-y-3">
          <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
            <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
            {{ eyebrow }}
          </div>
          <div>
            <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
              {{ title }}
            </h1>
            <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
              {{ description }}
            </p>
          </div>
        </div>

        <a
          [routerLink]="backRoute"
          class="inline-flex cursor-pointer items-center gap-2 self-start rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
          <lucide-icon name="arrow-left" class="h-4 w-4"></lucide-icon>
          {{ backLabel }}
        </a>
      </div>
    </section>

    <section class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      @for (card of shortcuts; track card.route) {
        <a
          [routerLink]="card.route"
          class="group rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-[#F7FBF3] hover:shadow-md">
          <div class="flex items-start justify-between gap-4">
            <div>
              <div class="inline-flex rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32] transition-colors group-hover:bg-[#E8F5E9]">
                <lucide-icon [name]="card.icon" class="h-5 w-5"></lucide-icon>
              </div>
              <h2 class="mt-4 text-base font-bold text-[#1B5E20]">{{ card.label }}</h2>
              <p class="mt-2 text-sm leading-6 text-[#5F6F5F]">{{ card.description }}</p>
            </div>
            <lucide-icon name="arrow-right" class="mt-1 h-4 w-4 text-[#7A8D7A] transition-transform group-hover:translate-x-0.5 group-hover:text-[#2E7D32]"></lucide-icon>
          </div>
          <p class="mt-4 text-xs font-semibold uppercase tracking-[0.12em] text-[#7A8D7A]">{{ card.route }}</p>
        </a>
      }
    </section>

    <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-5 shadow-sm">
      <div class="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h2 class="text-lg font-bold text-[#1B5E20]">Fase 1</h2>
          <p class="text-sm text-[#6B7D6C]">
            Solo navegación, permisos visuales y placeholders. El formulario y las acciones vendrán en la siguiente fase.
          </p>
        </div>
        <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-[#F7FBF3] px-3 py-1 text-xs font-semibold text-[#2E7D32]">
          <lucide-icon name="sparkles" class="h-3.5 w-3.5"></lucide-icon>
          {{ footerNote }}
        </div>
      </div>
    </section>
  `
})
export class ServiceModulePlaceholderComponent {
  @Input({ required: true }) eyebrow = '';
  @Input({ required: true }) title = '';
  @Input({ required: true }) description = '';
  @Input({ required: true }) backLabel = '';
  @Input({ required: true }) backRoute = '';
  @Input({ required: true }) footerNote = '';
  @Input({ required: true }) shortcuts: ServiceModuleShortcut[] = [];
}
