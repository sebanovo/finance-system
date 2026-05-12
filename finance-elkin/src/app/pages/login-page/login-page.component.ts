import { Component, effect, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoginFormComponent, LoginUseCase } from '../../features/auth';
import { LoginRequest } from '../../entities/auth';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, LoginFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-muted/30">
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
  private readonly router = inject(Router);

  constructor() {
    // Escuchar el estado de éxito para navegar al dashboard
    effect(() => {
      if (this.loginUseCase.status() === 'success') {
        this.router.navigate(['/dashboard']); // Ajustar a la ruta real de tu dashboard
      }
    });
  }

  ngOnInit(): void {
    this.loginUseCase.resetState();
  }

  handleLogin(request: LoginRequest): void {
    this.loginUseCase.login(request);
  }
}
