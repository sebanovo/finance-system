import { Component, computed, inject, output } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { HlmLabelImports } from "@shared/ui/label";
import { EmailInput } from "../../../../shared/custom-components/email-input/email-input";
import { PasswordStore } from "../../store/password.store";

@Component({
  selector: "app-forgot-password-form",
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmInputImports,
    HlmLabelImports,
    HlmFieldImports,
    EmailInput,
  ],
  templateUrl: "./forgot-password-form.html",
  styleUrl: "./forgot-password-form.css",
})
export class ForgotPasswordForm {
  private readonly formBuilder = inject(FormBuilder);
  private readonly passwordStore = inject(PasswordStore);

  readonly success = output<void>();

  readonly isSubmitting = this.passwordStore.forgotSubmitting;
  readonly errorMessage = computed(
    () => this.passwordStore.forgotError()?.message ?? null
  );

  readonly form = this.formBuilder.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    // tenantSlug: ["", [Validators.minLength(2)]],
  });

  constructor() {
    this.form.valueChanges.subscribe(() => {
      if (this.passwordStore.forgotError()) {
        this.passwordStore.clearForgotError();
      }
    });
  }

  get emailControl() {
    return this.form.controls.email;
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const success = await this.passwordStore.forgotPassword(
      this.form.getRawValue()
    );

    if (!success) {
      return;
    }

    this.success.emit();
  }
}
