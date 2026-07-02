import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginUseCase, FaceLoginUseCase } from '../../features/auth';
import { LoginFormComponent } from '../../features/auth/ui/login-form/login-form.component';
import { FaceLoginRequest, LoginRequest } from '../../entities/auth';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';
import { PendingCheckoutStorageService } from '../../entities/billing';
import { environment } from '../../../environments/environment';

interface PostLoginSummary {
  name: string;
  email: string;
  photoUrl: string | null;
  lastAccessLabel: string;
}

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, LoginFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-[#F1F8E9]">
      <div class="w-full max-w-md space-y-4">
        <div *ngIf="passwordResetSuccess" class="rounded-2xl border border-[#C8E6C9] bg-white px-4 py-3 text-sm font-medium text-[#1B5E20] shadow-sm">
          Tu contraseña fue actualizada. Ahora puedes iniciar sesión con la nueva clave.
        </div>

        <app-login-form
          [status]="displayStatus()"
          [error]="displayError()"
          (loginSubmit)="handleLogin($event)"
          (faceLoginSubmit)="handleFaceLogin($event)"
        />
      </div>

      <div
        *ngIf="postLoginSummary"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
        <div class="w-full max-w-lg overflow-hidden rounded-[28px] border border-[#111827] bg-white shadow-[0_24px_80px_rgba(0,0,0,0.35)]">
          <div class="border-b border-[#E5E7EB] px-6 py-5">
            <p class="text-xs font-bold uppercase tracking-[0.18em] text-[#166534]">Ingreso confirmado</p>
            <h2 class="mt-2 text-2xl font-black tracking-tight text-[#111827]">Bienvenido al tenant</h2>
            <p class="mt-1 text-sm text-[#6B7280]">Revisa tus datos antes de entrar.</p>
          </div>

          <div class="space-y-5 px-6 py-6">
            <div class="flex items-center gap-4 rounded-[24px] border border-[#E5E7EB] bg-[#F9FAFB] p-4">
              <div class="h-20 w-20 shrink-0 overflow-hidden rounded-2xl border border-[#111827] bg-white">
                <ng-container *ngIf="postLoginSummary.photoUrl; else fallbackAvatar">
                  <img [src]="postLoginSummary.photoUrl" [alt]="postLoginSummary.name" class="h-full w-full object-cover" />
                </ng-container>
                <ng-template #fallbackAvatar>
                  <div class="flex h-full w-full items-center justify-center text-2xl font-black text-[#166534]">
                    {{ avatarInitials(postLoginSummary.name) }}
                  </div>
                </ng-template>
              </div>

              <div class="min-w-0 flex-1">
                <p class="text-xs font-bold uppercase tracking-[0.16em] text-[#6B7280]">Usuario</p>
                <p class="truncate text-xl font-black text-[#111827]">{{ postLoginSummary.name }}</p>
                <p class="truncate text-sm text-[#6B7280]">{{ postLoginSummary.email }}</p>
              </div>
            </div>

            <div class="grid gap-3 sm:grid-cols-2">
              <div class="rounded-2xl border border-[#E5E7EB] bg-white p-4">
                <p class="text-xs font-bold uppercase tracking-[0.16em] text-[#6B7280]">Último acceso</p>
                <p class="mt-2 text-sm font-semibold text-[#111827]">{{ postLoginSummary.lastAccessLabel }}</p>
              </div>
              <div class="rounded-2xl border border-[#E5E7EB] bg-white p-4">
                <p class="text-xs font-bold uppercase tracking-[0.16em] text-[#6B7280]">Estado</p>
                <p class="mt-2 text-sm font-semibold text-[#166534]">Sesión activa</p>
              </div>
            </div>

            <button
              type="button"
              (click)="confirmPostLogin()"
              class="inline-flex w-full items-center justify-center rounded-full bg-[#166534] px-5 py-3 text-sm font-semibold text-white transition-colors hover:bg-[#14532D]">
              OK
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginPageComponent implements OnInit {
  public readonly loginUseCase = inject(LoginUseCase);
  public readonly faceLoginUseCase = inject(FaceLoginUseCase);
  private readonly authFacade = inject(AuthFacade);
  private readonly pendingCheckoutStorage = inject(PendingCheckoutStorageService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  passwordResetSuccess = false;
  postLoginSummary: PostLoginSummary | null = null;
  private pendingLandingRoute: string | null = null;

  ngOnInit(): void {
    this.loginUseCase.resetState();
    this.faceLoginUseCase.resetState();
    this.pendingCheckoutStorage.clear();
    this.passwordResetSuccess = this.route.snapshot.queryParamMap.get('passwordReset') === 'success';
  }

  async handleLogin(request: LoginRequest): Promise<void> {
    this.faceLoginUseCase.resetState();
    await this.loginUseCase.login(request);

    if (this.loginUseCase.status() !== 'success') {
      return;
    }

    await this.authFacade.loadCurrentUser();
    this.pendingLandingRoute = this.authFacade.getTenantLandingRoute();
    this.postLoginSummary = this.buildPostLoginSummary(request.email);
  }

  async handleFaceLogin(request: FaceLoginRequest): Promise<void> {
    this.loginUseCase.resetState();
    await this.faceLoginUseCase.login(request);

    if (this.faceLoginUseCase.status() !== 'success') {
      return;
    }

    await this.authFacade.loadCurrentUser();
    this.pendingLandingRoute = this.authFacade.getTenantLandingRoute();
    this.postLoginSummary = this.buildPostLoginSummary(request.email);
  }

  displayStatus(): 'idle' | 'loading' | 'success' | 'error' {
    if (this.loginUseCase.status() === 'loading' || this.faceLoginUseCase.status() === 'loading') {
      return 'loading';
    }

    if (this.loginUseCase.status() === 'error' || this.faceLoginUseCase.status() === 'error') {
      return 'error';
    }

    if (this.loginUseCase.status() === 'success' || this.faceLoginUseCase.status() === 'success') {
      return 'success';
    }

    return 'idle';
  }

  displayError(): string | null {
    return this.faceLoginUseCase.error() || this.loginUseCase.error();
  }

  confirmPostLogin(): void {
    const targetRoute = this.pendingLandingRoute ?? this.authFacade.getTenantLandingRoute();
    this.postLoginSummary = null;
    this.pendingLandingRoute = null;
    void this.router.navigateByUrl(targetRoute, { replaceUrl: true });
  }

  avatarInitials(name: string): string {
    const trimmed = name.trim();
    if (!trimmed) return 'U';
    const parts = trimmed.split(/\s+/).filter(Boolean).slice(0, 2);
    return parts.map((part) => part[0]?.toUpperCase() ?? '').join('') || trimmed[0].toUpperCase();
  }

  private buildPostLoginSummary(email: string): PostLoginSummary {
    const currentUser = this.authFacade.currentUser();
    const name = currentUser
      ? [currentUser.firstName, currentUser.lastName].filter(Boolean).join(' ').trim() || email.split('@')[0]
      : email.split('@')[0];
    const photoUrl = currentUser?.profilePhotoUrl
      ? `${environment.apiUrl}${currentUser.profilePhotoUrl}${currentUser.updatedAt ? `?v=${encodeURIComponent(currentUser.updatedAt)}` : ''}`
      : null;

    return {
      name,
      email: currentUser?.email ?? email,
      photoUrl,
      lastAccessLabel: new Date().toLocaleString('es-BO', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    };
  }
}
