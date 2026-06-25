import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-public-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <header class="mx-auto flex w-full max-w-7xl items-center justify-between px-4 py-5 sm:px-6 lg:px-8">
      <a routerLink="/" class="flex items-center gap-3">
        <img src="/logo.png" alt="Finance Web" class="h-11 w-11 rounded-2xl object-contain shadow-sm ring-1 ring-[#d7ead5]" />
        <div class="leading-tight">
          <p class="text-lg font-black tracking-tight text-[#1b5e20]">Finance Web</p>
          <p class="text-xs font-medium uppercase tracking-[0.22em] text-[#6b7d6c]">Plataforma financiera</p>
        </div>
      </a>

      <nav class="hidden items-center gap-3 md:flex">
        @if (mode === 'landing') {
          <a href="#videos" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Tutoriales</a>
          <a routerLink="/prices" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Precios</a>
          <a href="#canal" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Canal</a>
        } @else {
          <a routerLink="/" fragment="videos" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Tutoriales</a>
          <a routerLink="/" fragment="canal" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Canal</a>
          <a routerLink="/" class="rounded-full px-4 py-2 text-sm font-semibold text-[#2e7d32] transition-colors hover:bg-[#eaf6eb]">Inicio</a>
        }
        <a routerLink="/login" class="rounded-full bg-[#2e7d32] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#256428]">Ingresar</a>
      </nav>
    </header>
  `
})
export class PublicNavbarComponent {
  @Input() mode: 'landing' | 'prices' = 'landing';
}
