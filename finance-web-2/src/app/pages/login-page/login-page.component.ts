import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoginUseCase, FaceLoginUseCase } from '../../features/auth';
import { LoginFormComponent } from '../../features/auth/ui/login-form/login-form.component';
import { FaceLoginRequest, LoginRequest } from '../../entities/auth';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, LoginFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-[#F1F8E9]">
      <app-login-form
        [status]="displayStatus()"
        [error]="displayError()"
        (loginSubmit)="handleLogin($event)"
        (faceLoginSubmit)="handleFaceLogin($event)"
      />
    </div>
  `
})
export class LoginPageComponent implements OnInit {
  public readonly loginUseCase = inject(LoginUseCase);
  public readonly faceLoginUseCase = inject(FaceLoginUseCase);
  private readonly authFacade = inject(AuthFacade);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.loginUseCase.resetState();
    this.faceLoginUseCase.resetState();
  }

  async handleLogin(request: LoginRequest): Promise<void> {
    this.faceLoginUseCase.resetState();
    await this.loginUseCase.login(request);

    if (this.loginUseCase.status() !== 'success') {
      return;
    }

    void this.authFacade.loadCurrentUser();
    await this.router.navigateByUrl(this.authFacade.getTenantLandingRoute(), {
      replaceUrl: true
    });
  }

  async handleFaceLogin(request: FaceLoginRequest): Promise<void> {
    this.loginUseCase.resetState();
    await this.faceLoginUseCase.login(request);

    if (this.faceLoginUseCase.status() !== 'success') {
      return;
    }

    void this.authFacade.loadCurrentUser();
    await this.router.navigateByUrl(this.authFacade.getTenantLandingRoute(), {
      replaceUrl: true
    });
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
}
