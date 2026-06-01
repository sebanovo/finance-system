import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoginFormComponent, LoginUseCase } from '../../features/auth';
import { LoginRequest } from '../../entities/auth';
import { AuthFacade } from '../../shared/lib/auth/auth.facade';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, LoginFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-[#F1F8E9]">
      <app-login-form
        [status]="loginUseCase.status()"
        [error]="loginUseCase.error()"
        (loginSubmit)="handleLogin($event)"
      />
    </div>
  `
})
export class LoginPageComponent implements OnInit {
  public readonly loginUseCase = inject(LoginUseCase);
  private readonly authFacade = inject(AuthFacade);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.loginUseCase.resetState();
  }

  async handleLogin(request: LoginRequest): Promise<void> {
    await this.loginUseCase.login(request);

    if (this.loginUseCase.status() !== 'success') {
      return;
    }

    void this.authFacade.loadCurrentUser();
    console.log("handle login")
    await this.router.navigateByUrl(this.authFacade.getTenantLandingRoute(), {
      replaceUrl: true
    });
  }
}
