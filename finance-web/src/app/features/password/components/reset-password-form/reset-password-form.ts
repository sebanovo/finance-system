import {
  Component,
  computed,
  effect,
  inject,
  input,
  output,
} from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { HlmLabelImports } from "@shared/ui/label";
import { PasswordInput } from "../../../../shared/custom-components/password-input/password-input";
import { PasswordStore } from "../../store/password.store";

@Component({
  selector: "app-reset-password-form",
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmInputImports,
    HlmLabelImports,
    HlmFieldImports,
    PasswordInput,
  ],
  templateUrl: "./reset-password-form.html",
  styleUrl: "./reset-password-form.css",
})
export class ResetPasswordForm {
  private readonly formBuilder = inject(FormBuilder);

  readonly initialToken = input("");
  private readonly passwordStore = inject(PasswordStore);

  readonly success = output<void>();

  readonly isSubmitting = this.passwordStore.resetSubmitting;
  readonly errorMessage = computed(
    () => this.passwordStore.resetError()?.message ?? null
  );

  readonly form = this.formBuilder.nonNullable.group({
    token: ["", [Validators.required]],
    newPassword: ["", [Validators.required, Validators.minLength(8)]],
    confirmNewPassword: ["", [Validators.required]],
  });

  constructor() {
    effect(() => {
      const token = this.initialToken();
      if (token) {
        this.form.controls.token.setValue(token);
      }
    });

    this.form.valueChanges.subscribe(() => {
      if (this.passwordStore.resetError()) {
        this.passwordStore.clearResetError();
      }
    });
  }

  get tokenControl() {
    return this.form.controls.token;
  }

  get newPasswordControl() {
    return this.form.controls.newPassword;
  }

  get confirmNewPasswordControl() {
    return this.form.controls.confirmNewPassword;
  }

  get passwordsDoNotMatch(): boolean {
    const newPassword = this.newPasswordControl.value;
    const confirmNewPassword = this.confirmNewPasswordControl.value;

    return (
      this.confirmNewPasswordControl.touched &&
      !!newPassword &&
      !!confirmNewPassword &&
      newPassword !== confirmNewPassword
    );
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid || this.passwordsDoNotMatch) {
      this.form.markAllAsTouched();
      return;
    }

    const success = await this.passwordStore.resetPassword(
      this.form.getRawValue()
    );

    if (!success) {
      return;
    }

    this.success.emit();
  }
}
