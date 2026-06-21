import { Component, inject, OnInit } from '@angular/core';
import { SignupFormComponent, SignupUseCase } from '../../features/onboarding';
import { PublicSignupRequest } from '../../entities/tenant';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-onboarding-page',
  standalone: true,
  imports: [CommonModule, SignupFormComponent],
  template: `
    <div class="min-h-screen flex items-center justify-center p-4 bg-muted/30">
      <app-signup-form
        [loading]="signupUseCase.loading()"
        [error]="signupUseCase.error()"
        [success]="signupUseCase.success()"
        (formSubmit)="handleSignup($event)"
      />
    </div>
  `
})
export class OnboardingPageComponent implements OnInit {
  // Inyección del caso de uso (Clean Architecture)
  public readonly signupUseCase = inject(SignupUseCase);

  ngOnInit(): void {
    // Limpiamos el estado al entrar a la página
    this.signupUseCase.resetState();
  }

  handleSignup(request: PublicSignupRequest): void {
    // Delegamos la lógica de negocio al UseCase
    this.signupUseCase.signup(request);
  }
}
