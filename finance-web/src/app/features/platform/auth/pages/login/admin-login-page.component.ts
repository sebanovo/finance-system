import { Component, inject } from '@angular/core';
import { PlatformAuthStore } from '../../store/platform-auth.store';
import { AdminLoginFormComponent } from '../../components/login/admin-login-form.component';
import { PlatformLoginRequest } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-admin-login-page',
  standalone: true,
  imports: [AdminLoginFormComponent],
  template: `
    <main class="min-h-screen flex bg-[#ffffff]">
      
      <section class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 lg:p-24" aria-labelledby="login-title">
        <div class="w-full max-w-md">
          
          <header class="mb-10">
            <div class="h-10 w-10 bg-[#333333] rounded-lg flex items-center justify-center mb-4 shadow-lg" aria-hidden="true">
              <svg class="w-6 h-6 text-[#ffffff]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
              </svg>
            </div>
            <h1 id="login-title" class="text-3xl font-extrabold text-[#333333] tracking-tight">Acceso Superadmin</h1>
            <p class="text-[#666666] mt-2 text-sm">Control centralizado del ecosistema financiero.</p>
          </header>

          <app-admin-login-form 
            [isLoading]="store.isLoading()"
            [errorMessage]="store.error()"
            (submitForm)="onLogin($event)">
          </app-admin-login-form>

          <footer class="mt-8 pt-6 border-t border-[#cccccc] text-center">
            <p class="text-xs text-[#999999]">
              &copy; 2024 Plataforma Financiera SaaS. Todos los derechos reservados.
            </p>
          </footer>
        </div>
      </section>

      <aside class="hidden lg:flex w-1/2 bg-[#cccccc] relative overflow-hidden items-center justify-center" aria-hidden="true">
        <div class="absolute inset-0 opacity-20 bg-[radial-gradient(#999999_1px,transparent_1px)] [background-size:20px_20px]"></div>
        
        <figure class="relative z-10 w-3/4 h-3/4 bg-[#ffffff] rounded-2xl shadow-2xl flex flex-col overflow-hidden border border-[#999999]/30">
          <header class="h-12 border-b border-[#cccccc] flex items-center px-4 gap-2 bg-[#ffffff]">
            <div class="w-3 h-3 rounded-full bg-[#cccccc]"></div>
            <div class="w-3 h-3 rounded-full bg-[#999999]"></div>
            <div class="w-3 h-3 rounded-full bg-[#666666]"></div>
          </header>
          <div class="flex-1 flex p-4 gap-4 bg-gray-50/50">
            <nav class="w-1/4 flex flex-col gap-3">
              <div class="h-4 bg-[#cccccc] rounded w-full"></div>
              <div class="h-4 bg-[#cccccc] rounded w-5/6"></div>
              <div class="h-4 bg-[#cccccc] rounded w-4/6"></div>
            </nav>
            <article class="w-3/4 flex flex-col gap-4">
              <div class="h-24 bg-[#ffffff] border border-[#cccccc] rounded-xl shadow-sm"></div>
              <div class="flex-1 bg-[#ffffff] border border-[#cccccc] rounded-xl shadow-sm"></div>
            </article>
          </div>
          <figcaption class="sr-only">Representación visual de la interfaz de gestión financiera</figcaption>
        </figure>
      </aside>
      
    </main>
  `
})

export class AdminLoginPageComponent {
  readonly store = inject(PlatformAuthStore);
  onLogin(credentials: PlatformLoginRequest) {
    this.store.login(credentials);
  }
}