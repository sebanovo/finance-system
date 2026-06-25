import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { SignupFormComponent, SignupUseCase } from '../../features/onboarding';
import { PublicPaidSignupRequest, PublicSignupRequest } from '../../entities/tenant';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

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
        [selectedPlanCode]="selectedPlanCode()"
        [selectedBillingInterval]="selectedBillingInterval()"
        (formSubmit)="handleSignup($event)"
      />
    </div>
  `
})
export class OnboardingPageComponent implements OnInit {
  public readonly signupUseCase = inject(SignupUseCase);
  private readonly route = inject(ActivatedRoute);

  readonly selectedPlanCode = signal('DEMO');
  readonly selectedBillingInterval = signal<'MONTHLY' | 'YEARLY'>('MONTHLY');
  readonly isPaidSignup = computed(() => {
    const plan = this.selectedPlanCode().toUpperCase();
    return plan !== 'DEMO' && plan !== 'ENTERPRISE';
  });

  ngOnInit(): void {
    this.signupUseCase.resetState();

    const query = this.route.snapshot.queryParamMap;
    const plan = (query.get('plan') || 'DEMO').trim().toUpperCase();
    const billing = (query.get('billing') || 'MONTHLY').trim().toUpperCase();

    this.selectedPlanCode.set(plan || 'DEMO');
    this.selectedBillingInterval.set(billing === 'YEARLY' ? 'YEARLY' : 'MONTHLY');
  }

  handleSignup(request: PublicSignupRequest): void {
    if (this.isPaidSignup()) {
      this.signupUseCase.signupPaid({
        ...request,
        planCode: this.selectedPlanCode(),
        billingInterval: this.selectedBillingInterval()
      } satisfies PublicPaidSignupRequest);
      return;
    }

    this.signupUseCase.signup(request);
  }
}
