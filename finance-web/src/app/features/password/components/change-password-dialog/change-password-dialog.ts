import { Component, inject, input, output } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideLock } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmDialogImports } from "@shared/ui/dialog";
import { HlmInputImports } from "@shared/ui/input";
import { HlmLabelImports } from "@shared/ui/label";
import { PasswordInput } from "../../../../shared/custom-components/password-input/password-input";
import { ChangePasswordFormValue } from "../../model/password.type";

@Component({
  selector: "app-change-password-dialog",
  imports: [
    ReactiveFormsModule,
    HlmAlertImports,
    HlmButtonImports,
    HlmDialogImports,
    HlmInputImports,
    HlmLabelImports,
    NgIcon,
    PasswordInput,
  ],
  providers: [provideIcons({ lucideLock })],
  templateUrl: "./change-password-dialog.html",
  styleUrl: "./change-password-dialog.css",
})
export class ChangePasswordDialog {
  private readonly fb = inject(FormBuilder);

  readonly open = input(false);
  readonly isSubmitting = input(false);
  readonly errorMessage = input<string | null>(null);

  readonly openChange = output<boolean>();
  readonly submitChangePassword = output<ChangePasswordFormValue>();
  readonly formEdited = output<void>();

  readonly form = this.fb.nonNullable.group({
    currentPassword: ["", [Validators.required]],
    newPassword: ["", [Validators.required, Validators.minLength(8)]],
    confirmNewPassword: ["", [Validators.required]],
  });

  get currentPasswordControl() {
    return this.form.controls.currentPassword;
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

  onSubmit(): void {
    if (this.form.invalid || this.passwordsDoNotMatch) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitChangePassword.emit(this.form.getRawValue());
  }

  onFormEdited(): void {
    this.formEdited.emit();
  }

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);

    if (!isOpen) {
      this.form.reset({
        currentPassword: "",
        newPassword: "",
        confirmNewPassword: "",
      });
    }
  }
}
