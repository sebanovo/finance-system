import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-landing-cta',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <section id="canal" class="px-4 pb-20 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-7xl rounded-[2rem] border border-[#cfe5cd] bg-[#12391a] px-6 py-8 text-white shadow-[0_18px_50px_rgba(18,57,26,0.16)] sm:px-8">
        <div class="grid gap-8 lg:grid-cols-[1.1fr_0.9fr] lg:items-center">
          <div class="space-y-4">
            <p class="text-xs font-semibold uppercase tracking-[0.24em] text-[#b8e6bc]">Canal de YouTube</p>
            <h2 class="text-3xl font-black tracking-tight sm:text-4xl">Síguenos para ver más videos, guías y novedades</h2>
            <p class="max-w-2xl text-base leading-7 text-[#d7ead5]">
              Encontrarás explicaciones rápidas sobre login con face-recognition, pagos de servicios, perfil de usuario, cuentas y otras funciones del sistema.
            </p>
          </div>

          <div class="flex flex-col gap-3 sm:flex-row lg:justify-end">
            <a [href]="youtubeChannelUrl" target="_blank" rel="noopener noreferrer" class="inline-flex items-center justify-center gap-2 rounded-2xl bg-white px-5 py-3 text-sm font-semibold text-[#12391a] transition-colors hover:bg-[#eef7ea]">
              <lucide-icon name="play" class="h-4 w-4"></lucide-icon>
              Abrir canal
            </a>
            <a routerLink="/login" class="inline-flex items-center justify-center gap-2 rounded-2xl border border-white/20 px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-white/10">
              <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
              Ir al login
            </a>
          </div>
        </div>
      </div>
    </section>
  `
})
export class LandingCtaComponent {
  @Input({ required: true }) youtubeChannelUrl = '';
}
