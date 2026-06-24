import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(46,125,50,0.14),_transparent_35%),radial-gradient(circle_at_top_right,_rgba(76,175,80,0.10),_transparent_30%),linear-gradient(180deg,_#ffffff_0%,_#f6fbf3_56%,_#eef7ea_100%)] text-[#12391a]">
      <header class="mx-auto flex w-full max-w-7xl items-center justify-between px-4 py-5 sm:px-6 lg:px-8">
        <a routerLink="/" class="flex items-center gap-3">
          <img src="/logo.png" alt="Finance Web" class="h-11 w-11 rounded-2xl object-contain shadow-sm ring-1 ring-[#d7ead5]" />
          <div class="leading-tight">
            <p class="text-lg font-black tracking-tight text-[#1b5e20]">Finance Web</p>
            <p class="text-xs font-medium uppercase tracking-[0.22em] text-[#6b7d6c]">Plataforma financiera</p>
          </div>
        </a>

        <nav class="hidden items-center gap-3 md:flex">
          <a href="#videos" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Tutoriales</a>
          <a href="#canal" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Canal</a>
          <a routerLink="/login" class="rounded-full bg-[#2e7d32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]">Ingresar</a>
        </nav>
      </header>

      <main class="mx-auto flex w-full max-w-7xl flex-col gap-14 px-4 pb-16 sm:px-6 lg:px-8">
        <section class="grid items-center gap-10 lg:grid-cols-[1.08fr_0.92fr] lg:pt-4">
          <div class="space-y-7">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#cfe5cd] bg-white/80 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-[#2e7d32] shadow-sm backdrop-blur">
              <lucide-icon name="sparkles" [size]="14"></lucide-icon>
              Guías rápidas y acceso seguro
            </div>

            <div class="space-y-5">
              <h1 class="max-w-3xl text-4xl font-black tracking-tight text-[#12391a] sm:text-5xl lg:text-6xl">
                Bienvenido a una experiencia financiera más clara, rápida y visual.
              </h1>
              <p class="max-w-2xl text-base leading-8 text-[#516456] sm:text-lg">
                Entra como usuario del tenant o como superadmin, gestiona tus cuentas y aprende con videos cortos cómo usar login con face-recognition y pagos de servicios.
              </p>
            </div>

            <div class="flex flex-col gap-3 sm:flex-row">
              <a routerLink="/login" class="inline-flex items-center justify-center gap-2 rounded-2xl bg-[#2e7d32] px-6 py-3.5 text-sm font-semibold text-white shadow-[0_12px_30px_rgba(46,125,50,0.22)] transition-all hover:-translate-y-0.5 hover:bg-[#256428]">
                <lucide-icon name="arrow-right" [size]="16"></lucide-icon>
                Ingresar al tenant
              </a>
              <a routerLink="/platform/login" class="inline-flex items-center justify-center gap-2 rounded-2xl border border-[#cfe5cd] bg-white px-6 py-3.5 text-sm font-semibold text-[#2e7d32] shadow-sm transition-all hover:-translate-y-0.5 hover:bg-[#f4faf2]">
                <lucide-icon name="shield-check" [size]="16"></lucide-icon>
                Entrar como superadmin
              </a>
            </div>

            <div class="grid gap-4 sm:grid-cols-3">
              <article class="rounded-3xl border border-[#d9e8d7] bg-white p-5 shadow-sm">
                <div class="mb-3 inline-flex rounded-2xl bg-[#eef7ea] p-3 text-[#2e7d32]">
                  <lucide-icon name="users" [size]="22"></lucide-icon>
                </div>
                <h2 class="text-sm font-bold text-[#1b5e20]">Multitenant</h2>
                <p class="mt-2 text-sm leading-6 text-[#5f6f5f]">Cada tenant con su propio contexto, usuarios y datos aislados.</p>
              </article>

              <article class="rounded-3xl border border-[#d9e8d7] bg-white p-5 shadow-sm">
                <div class="mb-3 inline-flex rounded-2xl bg-[#eef7ea] p-3 text-[#2e7d32]">
                  <lucide-icon name="camera" [size]="22"></lucide-icon>
                </div>
                <h2 class="text-sm font-bold text-[#1b5e20]">Face-recognition</h2>
                <p class="mt-2 text-sm leading-6 text-[#5f6f5f]">Activa el acceso con rostro y usa la foto del perfil.</p>
              </article>

              <article class="rounded-3xl border border-[#d9e8d7] bg-white p-5 shadow-sm">
                <div class="mb-3 inline-flex rounded-2xl bg-[#eef7ea] p-3 text-[#2e7d32]">
                  <lucide-icon name="play" [size]="22"></lucide-icon>
                </div>
                <h2 class="text-sm font-bold text-[#1b5e20]">Tutoriales</h2>
                <p class="mt-2 text-sm leading-6 text-[#5f6f5f]">Videos cortos para empezar en minutos, sin perder tiempo.</p>
              </article>
            </div>
          </div>

          <div class="relative">
            <div class="absolute -left-6 top-10 h-40 w-40 rounded-full bg-[#2e7d32]/10 blur-3xl"></div>
            <div class="absolute -right-6 bottom-4 h-56 w-56 rounded-full bg-[#4caf50]/10 blur-3xl"></div>

            <div class="relative overflow-hidden rounded-[2rem] border border-[#dbead8] bg-white/90 p-6 shadow-[0_20px_60px_rgba(27,94,32,0.10)] backdrop-blur">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p class="text-xs font-semibold uppercase tracking-[0.22em] text-[#6b7d6c]">Inicio rápido</p>
                  <h2 class="mt-2 text-2xl font-black text-[#12391a]">Aprende lo básico y entra con seguridad</h2>
                </div>
                <div class="rounded-2xl bg-[#eef7ea] p-3 text-[#2e7d32]">
                  <lucide-icon name="book-open" [size]="24"></lucide-icon>
                </div>
              </div>

              <div class="mt-6 space-y-4">
                <div class="rounded-2xl border border-[#dbead8] bg-[#f9fcf8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.18em] text-[#6b7d6c]">01. Acceso seguro</p>
                  <p class="mt-2 text-sm leading-6 text-[#516456]">Ingresa con contraseña o con rostro, según tu configuración y el tenant activo.</p>
                </div>
                <div class="rounded-2xl border border-[#dbead8] bg-[#f9fcf8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.18em] text-[#6b7d6c]">02. Pagos de servicios</p>
                  <p class="mt-2 text-sm leading-6 text-[#516456]">Consulta, paga y revisa tus pagos de servicios desde una sola interfaz.</p>
                </div>
                <div class="rounded-2xl border border-[#dbead8] bg-[#f9fcf8] p-4">
                  <p class="text-xs font-semibold uppercase tracking-[0.18em] text-[#6b7d6c]">03. Tu perfil</p>
                  <p class="mt-2 text-sm leading-6 text-[#516456]">Actualiza tu foto, usa cámara o galería y activa login facial si lo necesitas.</p>
                </div>
              </div>

              <div class="mt-6 flex flex-wrap gap-3">
                <a href="#videos" class="inline-flex items-center gap-2 rounded-2xl border border-[#cfe5cd] bg-white px-5 py-3 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#f4faf2]">
                  <lucide-icon name="play" [size]="16"></lucide-icon>
                  Ver tutoriales
                </a>
                <a [href]="youtubeChannelUrl" target="_blank" rel="noopener noreferrer" class="inline-flex items-center gap-2 rounded-2xl bg-[#1b5e20] px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-[#124119]">
                  <lucide-icon name="arrow-right" [size]="16"></lucide-icon>
                  Ir al canal de YouTube
                </a>
              </div>
            </div>
          </div>
        </section>

        <section id="videos" class="space-y-6">
          <div class="max-w-3xl space-y-3">
            <p class="text-xs font-semibold uppercase tracking-[0.24em] text-[#6b7d6c]">Tutoriales destacados</p>
            <h2 class="text-3xl font-black tracking-tight text-[#12391a]">Mira los videos más útiles para empezar</h2>
            <p class="text-base leading-7 text-[#516456]">
              Te recomiendo ver estos dos primero. Si te sirven, suscríbete al canal para encontrar más guías cortas sobre la plataforma.
            </p>
          </div>

          <div class="grid gap-6 lg:grid-cols-2">
            @for (video of videos; track video.title) {
              <article class="overflow-hidden rounded-[2rem] border border-[#dbead8] bg-white shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
                <div class="aspect-video bg-[#0f2614]">
                  <iframe
                    class="h-full w-full"
                    [src]="video.safeUrl"
                    [title]="video.title"
                    frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    referrerpolicy="strict-origin-when-cross-origin"
                    allowfullscreen></iframe>
                </div>
                <div class="space-y-4 p-5 sm:p-6">
                  <div class="inline-flex rounded-full bg-[#eef7ea] px-3 py-1 text-xs font-semibold uppercase tracking-[0.18em] text-[#2e7d32]">
                    {{ video.badge }}
                  </div>
                  <div>
                    <h3 class="text-xl font-black text-[#12391a]">{{ video.title }}</h3>
                    <p class="mt-2 text-sm leading-6 text-[#5f6f5f]">{{ video.description }}</p>
                  </div>
                  <div class="flex flex-wrap gap-3">
                    <a [href]="videoLink(video)" target="_blank" rel="noopener noreferrer" class="inline-flex items-center gap-2 rounded-2xl bg-[#2e7d32] px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
                      <lucide-icon name="play" [size]="16"></lucide-icon>
                      Ver en YouTube
                    </a>
                    <a [href]="youtubeChannelUrl" target="_blank" rel="noopener noreferrer" class="inline-flex items-center gap-2 rounded-2xl border border-[#cfe5cd] bg-white px-4 py-2.5 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#f4faf2]">
                      <lucide-icon name="sparkles" [size]="16"></lucide-icon>
                      Más videos del canal
                    </a>
                  </div>
                </div>
              </article>
            }
          </div>
        </section>

        <section id="canal" class="rounded-[2rem] border border-[#cfe5cd] bg-[#12391a] px-6 py-8 text-white shadow-[0_18px_50px_rgba(18,57,26,0.16)] sm:px-8">
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
                <lucide-icon name="play" [size]="16"></lucide-icon>
                Abrir canal
              </a>
              <a routerLink="/login" class="inline-flex items-center justify-center gap-2 rounded-2xl border border-white/20 px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-white/10">
                <lucide-icon name="arrow-right" [size]="16"></lucide-icon>
                Ir al login
              </a>
            </div>
          </div>
        </section>
      </main>
    </div>
  `
})
export class LandingPageComponent {
  readonly youtubeChannelUrl = 'https://youtube.com/@prospera-6777?si=Bb9wP5j311yJgtKF';
  readonly videos: Array<{ title: string; description: string; url: string; badge: string; safeUrl: SafeResourceUrl }> = [];

  constructor(private readonly sanitizer: DomSanitizer) {
    const items = [
      {
        title: 'Como iniciar sesión con face-recognition',
        description: 'Aprende cómo ingresar con rostro usando la foto de perfil y el flujo de acceso facial del sistema.',
        url: 'https://www.youtube.com/embed/RgVVV59qzMI?si=2qieICAG3MGf8-VC',
        badge: 'Acceso con rostro'
      },
      {
        title: 'Como pagar servicios',
        description: 'Mira cómo registrar y pagar servicios desde la experiencia del tenant, paso a paso.',
        url: 'https://www.youtube.com/embed/Q907PF4RQXU?si=FMCvZAtQ_IRFxTUC',
        badge: 'Pagos de servicios'
      }
    ];

    this.videos = items.map((video) => ({
      ...video,
      safeUrl: this.sanitizer.bypassSecurityTrustResourceUrl(video.url)
    }));
  }

  videoLink(video: { url: string }): string {
    return video.url.replace('embed/', 'watch?v=').split('?')[0];
  }
}
