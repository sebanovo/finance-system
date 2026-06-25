import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { LucideAngularModule } from 'lucide-angular';
import { LandingVideoCard } from '../../model/landing-plan.model';

@Component({
  selector: 'app-landing-videos',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  template: `
    <section id="videos" class="bg-[#F7FBF5] px-4 pb-20 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-7xl space-y-6">
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
                    <lucide-icon name="play" class="h-4 w-4"></lucide-icon>
                    Ver en YouTube
                  </a>
                  <a [href]="youtubeChannelUrl" target="_blank" rel="noopener noreferrer" class="inline-flex items-center gap-2 rounded-2xl border border-[#cfe5cd] bg-white px-4 py-2.5 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#f4faf2]">
                    <lucide-icon name="sparkles" class="h-4 w-4"></lucide-icon>
                    Más videos del canal
                  </a>
                </div>
              </div>
            </article>
          }
        </div>
      </div>
    </section>
  `
})
export class LandingVideosComponent {
  @Input({ required: true }) videos: Array<LandingVideoCard & { safeUrl: SafeResourceUrl }> = [];
  @Input({ required: true }) youtubeChannelUrl = '';

  videoLink(video: { url: string }): string {
    return video.url.replace('embed/', 'watch?v=').split('?')[0];
  }
}
