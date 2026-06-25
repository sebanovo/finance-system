import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { LandingAppDownloadData } from '../../model/landing-app-download.model';

@Component({
  selector: 'app-landing-app-download',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <section
      id="app-movil"
      class="relative isolate overflow-hidden bg-gradient-to-br from-[#B71C1C] via-[#A65334] to-[#1B6B2A] px-4 py-20 text-white sm:px-6 lg:px-8">

      <div class="absolute inset-0 -z-10 opacity-[0.14] [background-image:radial-gradient(#ffffff_1.2px,transparent_1.2px)] [background-size:32px_32px]"></div>
      <div class="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_20%_20%,rgba(255,255,255,0.16),transparent_26%),radial-gradient(circle_at_80%_70%,rgba(255,255,255,0.10),transparent_30%)]"></div>

      <div class="mx-auto grid max-w-7xl items-center gap-12 lg:grid-cols-[1fr_0.9fr]">
        <div>
          <div class="mb-7 inline-flex w-fit items-center gap-2 rounded-full bg-white/16 px-5 py-2 text-sm font-black text-white backdrop-blur">
            <lucide-icon name="smartphone" class="h-4 w-4"></lucide-icon>
            {{ data.badge }}
          </div>

          <h2 class="max-w-2xl text-4xl font-black leading-tight tracking-tight sm:text-5xl">
            {{ data.title }}
          </h2>

          <p class="mt-6 max-w-2xl text-lg leading-8 text-white/88">
            {{ data.description }}
          </p>

          <div class="mt-10 grid gap-5 sm:grid-cols-3">
            @for (feature of data.features; track feature.title) {
              <article class="text-center sm:text-left">
                <div class="mx-auto flex h-13 w-13 items-center justify-center rounded-2xl bg-white/16 text-white sm:mx-0">
                  <lucide-icon [name]="feature.icon" class="h-6 w-6"></lucide-icon>
                </div>
                <h3 class="mt-4 text-base font-black">{{ feature.title }}</h3>
                <p class="mt-2 text-sm leading-6 text-white/78">{{ feature.description }}</p>
              </article>
            }
          </div>

          <div class="mt-10 flex flex-col gap-3 sm:flex-row">
            <a
              [href]="data.driveDownloadUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="inline-flex h-12 items-center justify-center gap-3 rounded-2xl bg-white px-6 text-sm font-black text-[#1B5E20] shadow-sm transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="download" class="h-4 w-4"></lucide-icon>
              Descargar desde Drive
            </a>

            <a
              href="#tutoriales"
              class="inline-flex h-12 items-center justify-center gap-3 rounded-2xl bg-white/14 px-6 text-sm font-black text-white backdrop-blur transition-colors hover:bg-white/20">
              <lucide-icon name="play" class="h-4 w-4"></lucide-icon>
              Ver tutoriales
            </a>
          </div>

          <div class="mt-8 flex items-center gap-2 text-sm font-bold text-white/90">
            <lucide-icon name="star" class="h-4 w-4 fill-current text-yellow-300"></lucide-icon>
            <lucide-icon name="star" class="h-4 w-4 fill-current text-yellow-300"></lucide-icon>
            <lucide-icon name="star" class="h-4 w-4 fill-current text-yellow-300"></lucide-icon>
            <lucide-icon name="star" class="h-4 w-4 fill-current text-yellow-300"></lucide-icon>
            <lucide-icon name="star" class="h-4 w-4 fill-current text-yellow-300"></lucide-icon>
            <span class="ml-2">Disponible para descarga directa</span>
          </div>
        </div>

        <div class="flex justify-center lg:justify-end">
          <div class="w-full max-w-md rounded-[32px] bg-white/94 p-8 text-center text-[#111827] shadow-[0_30px_90px_rgba(0,0,0,0.22)]">
            <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-3xl bg-[#2E7D32] text-white">
              <lucide-icon name="smartphone" class="h-10 w-10"></lucide-icon>
            </div>

            <h3 class="mt-7 text-2xl font-black">Descarga rápida</h3>
            <p class="mx-auto mt-3 max-w-xs text-sm leading-6 text-[#4B5563]">
              Escanea el código QR para abrir el enlace de descarga desde Drive.
            </p>

            <div class="mx-auto mt-8 flex max-w-[250px] items-center justify-center rounded-[28px] border border-[#E5E7EB] bg-[#F9FAFB] p-5 shadow-inner">
              <img
                [src]="data.qrImageUrl"
                alt="Código QR para descargar la app móvil"
                class="h-48 w-48 object-contain" />
            </div>

            <p class="mt-6 text-sm text-[#6B7280]">
              Compatible con Android.
            </p>

            <p class="mt-2 inline-flex items-center gap-2 text-xs text-[#6B7280]">
              <span class="h-1.5 w-1.5 rounded-full bg-[#22C55E]"></span>
              Descarga directa y verificada
            </p>
          </div>
        </div>
      </div>
    </section>
  `
})
export class LandingAppDownloadComponent {
  @Input({ required: true }) data!: LandingAppDownloadData;
}
