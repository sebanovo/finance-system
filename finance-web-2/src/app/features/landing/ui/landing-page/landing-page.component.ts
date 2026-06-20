// features/landing/ui/landing-page/landing-page.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  template: `
    <div class="min-h-screen bg-linear-to-br from-slate-950 via-slate-900 to-[#1a3320] flex flex-col">

      <!-- Fondo decorativo con partículas sutiles -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-[#2E7D32]/5 rounded-full blur-3xl"></div>
        <div class="absolute bottom-1/4 right-1/4 w-80 h-80 bg-[#4CAF50]/5 rounded-full blur-3xl"></div>
      </div>

      <!-- Header logo -->
      <header class="relative z-10 flex items-center justify-between px-8 py-6">
        <div class="flex items-center gap-3">
          <img
            src="/logo.png"
            alt="Prospera"
            class="h-10 w-10 rounded-lg object-contain shadow-lg"
          >
          <span class="font-bold text-2xl tracking-tight text-white">PROSPERA</span>
        </div>
        <div class="text-sm text-slate-400">
          Plataforma SaaS Financiero
        </div>
      </header>

      <!-- Contenido central -->
      <main class="relative z-10 flex-1 flex flex-col items-center justify-center px-4 pb-12">

        <!-- Título principal -->
        <div class="text-center mb-14">
          <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-[#2E7D32]/20 border border-[#2E7D32]/30 text-[#4CAF50] text-sm font-medium mb-6">
            <lucide-icon name="shield" class="h-3.5 w-3.5"></lucide-icon>
            Acceso seguro y encriptado
          </div>
          <h1 class="text-4xl md:text-5xl font-extrabold text-white tracking-tight leading-tight">
            Bienvenido a tu<br>
            <span class="text-transparent bg-clip-text bg-linear-to-r from-[#4CAF50] to-[#81C784]">Panel de Control</span>
          </h1>
          <p class="mt-4 text-slate-400 text-lg max-w-md mx-auto">
            Selecciona el tipo de cuenta con la que deseas acceder al sistema.
          </p>
        </div>

        <!-- Tarjetas de acceso -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-2xl">

          <!-- Acceso Tenant (Admin) -->
          <a routerLink="/login" class="group relative block rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm p-8 hover:bg-white/10 hover:border-[#4CAF50]/40 transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl hover:shadow-[#2E7D32]/20 cursor-pointer">
            <!-- Icono -->
            <div class="h-14 w-14 rounded-xl bg-[#2E7D32]/20 border border-[#2E7D32]/30 flex items-center justify-center mb-6 group-hover:bg-[#2E7D32]/30 transition-colors">
              <lucide-icon name="building-2" class="h-7 w-7 text-[#4CAF50]"></lucide-icon>
            </div>
            <!-- Contenido -->
            <h2 class="text-xl font-bold text-white mb-2">Acceso Empresarial</h2>
            <p class="text-slate-400 text-sm leading-relaxed mb-6">
              Para administradores y usuarios de una organización. Gestiona tus cuentas, transacciones y equipo.
            </p>
            <!-- Características -->
            <ul class="space-y-2 mb-8">
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Gestión de usuarios y roles
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Panel de transacciones y cuentas
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-[#4CAF50] shrink-0"></lucide-icon>
                Contabilidad y tipos de cambio
              </li>
            </ul>
            <!-- CTA -->
            <div class="flex items-center gap-2 text-sm font-semibold text-[#4CAF50] group-hover:gap-3 transition-all">
              Iniciar sesión
              <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
            </div>
          </a>

          <!-- Acceso SuperAdmin (Platform) -->
          <a routerLink="/platform/login" class="group relative block rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm p-8 hover:bg-white/10 hover:border-purple-500/40 transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl hover:shadow-purple-900/20 cursor-pointer">
            <!-- Icono -->
            <div class="h-14 w-14 rounded-xl bg-purple-900/20 border border-purple-500/20 flex items-center justify-center mb-6 group-hover:bg-purple-900/30 transition-colors">
              <lucide-icon name="layout-dashboard" class="h-7 w-7 text-purple-400"></lucide-icon>
            </div>
            <!-- Badge -->
            <div class="absolute top-6 right-6">
              <span class="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-900/40 text-purple-300 border border-purple-500/30">
                SuperAdmin
              </span>
            </div>
            <!-- Contenido -->
            <h2 class="text-xl font-bold text-white mb-2">Administración Global</h2>
            <p class="text-slate-400 text-sm leading-relaxed mb-6">
              Acceso exclusivo para administradores de la plataforma. Gestiona tenants, planes y suscripciones.
            </p>
            <!-- Características -->
            <ul class="space-y-2 mb-8">
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Panel de métricas global
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Gestión de planes y suscripciones
              </li>
              <li class="flex items-center gap-2 text-xs text-slate-400">
                <lucide-icon name="check-circle" class="h-3.5 w-3.5 text-purple-400 shrink-0"></lucide-icon>
                Control de organizaciones (tenants)
              </li>
            </ul>
            <!-- CTA -->
            <div class="flex items-center gap-2 text-sm font-semibold text-purple-400 group-hover:gap-3 transition-all">
              Acceder como SuperAdmin
              <lucide-icon name="arrow-right" class="h-4 w-4"></lucide-icon>
            </div>
          </a>

        </div>

        <!-- Link a registro público -->
        <p class="mt-10 text-sm text-slate-500">
          ¿Eres nuevo? 
          <a routerLink="/onboarding" class="text-[#4CAF50] font-medium hover:underline hover:text-[#81C784] transition-colors">
            Regístrate y crea tu organización gratis
          </a>
        </p>

      </main>

      <!-- Footer -->
      <footer class="relative z-10 text-center pb-6 text-xs text-slate-600">
        © 2025 PROSPERA — Plataforma Financiera SaaS
      </footer>

    </div>
  `
})
export class LandingPageComponent {}
// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterModule } from '@angular/router';
// import { LucideAngularModule } from 'lucide-angular';

// @Component({
//   selector: 'app-landing-page',
//   standalone: true,
//   imports: [CommonModule, RouterModule, LucideAngularModule],
//   template: `
//     <div class="min-h-screen bg-white">
//       <!-- Navbar -->
//       <nav class="bg-white border-b border-[#C8E6C9] sticky top-0 z-50">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
//           <div class="flex justify-between items-center h-16">
//             <div class="flex items-center">
//               <div class="h-8 w-8 rounded-md bg-[#2E7D32] flex items-center justify-center">
//                 <span class="text-white font-bold text-lg">F</span>
//               </div>
//               <span class="ml-2 text-xl font-bold text-[#2E7D32]">Finance System</span>
//             </div>
//             <div class="flex items-center gap-4">
//               <a routerLink="/login" class="text-[#2E7D32] hover:text-[#4CAF50] px-3 py-2 text-sm font-medium">
//                 Iniciar Sesión
//               </a>
//               <a routerLink="/onboarding" class="bg-[#2E7D32] text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-[#4CAF50] transition-colors">
//                 Crear Cuenta
//               </a>
//             </div>
//           </div>
//         </div>
//       </nav>

//       <!-- Hero -->
//       <div class="relative bg-[#F1F8E9] overflow-hidden">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
//           <div class="text-center">
//             <h1 class="text-4xl md:text-6xl font-bold text-[#2E7D32] tracking-tight">
//               Gestión financiera
//               <span class="text-[#4CAF50]">a nivel corporativo</span>
//             </h1>
//             <p class="mt-6 text-lg text-[#666666] max-w-2xl mx-auto">
//               Centraliza la administración de finanzas, usuarios y roles en una sola plataforma.
//               Diseñado con arquitectura robusta para escalar junto a los objetivos de tu empresa.
//             </p>
//             <div class="mt-10 flex flex-col sm:flex-row justify-center gap-4">
//               <a routerLink="/onboarding" class="bg-[#2E7D32] text-white px-6 py-3 rounded-lg font-medium hover:bg-[#4CAF50] transition-colors">
//                 Comenzar prueba gratis
//               </a>
//               <a routerLink="/login" class="border border-[#C8E6C9] text-[#2E7D32] px-6 py-3 rounded-lg font-medium hover:bg-[#F1F8E9] transition-colors">
//                 Iniciar sesión
//               </a>
//             </div>
//           </div>
//         </div>
//       </div>

//       <!-- Features -->
//       <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
//         <div class="text-center mb-12">
//           <h2 class="text-3xl font-bold text-[#2E7D32]">Características</h2>
//           <p class="mt-4 text-[#666666]">Todo lo que necesitas para administrar tu negocio</p>
//         </div>
//         <div class="grid md:grid-cols-3 gap-8">
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="users" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Gestión de Usuarios</h3>
//             <p class="mt-2 text-[#666666] text-sm">Administra usuarios con control granular de permisos y roles.</p>
//           </div>
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="shield" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Control de Acceso</h3>
//             <p class="mt-2 text-[#666666] text-sm">Define roles personalizados y permisos específicos.</p>
//           </div>
//           <div class="bg-white p-6 rounded-xl border border-[#C8E6C9] shadow-sm">
//             <div class="h-12 w-12 rounded-lg bg-[#E8F5E9] flex items-center justify-center mb-4">
//               <lucide-icon name="building" class="h-6 w-6 text-[#2E7D32]"></lucide-icon>
//             </div>
//             <h3 class="text-lg font-semibold text-[#2E7D32]">Multitenencia</h3>
//             <p class="mt-2 text-[#666666] text-sm">Soporta múltiples empresas con aislamiento de datos total.</p>
//           </div>
//         </div>
//       </div>

//       <!-- Footer -->
//       <footer class="bg-[#F1F8E9] border-t border-[#C8E6C9] py-8">
//         <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
//           <p class="text-[#666666] text-sm">© 2024 Finance System. Todos los derechos reservados.</p>
//           <div class="mt-4">
//             <a routerLink="/platform/login" class="text-xs text-[#4CAF50] hover:text-[#2E7D32]">Acceso SuperAdmin</a>
//           </div>
//         </div>
//       </footer>
//     </div>
//   `
// })
// export class LandingPageComponent {}
