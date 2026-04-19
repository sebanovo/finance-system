import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toChangePasswordRequest,
  toForgotPasswordRequest,
  toResetPasswordRequest,
} from "../adapters/password.adapter";
import {
  ChangePasswordFormValue,
  ForgotPasswordFormValue,
  ResetPasswordFormValue,
} from "../model/password.type";
import { PasswordApi } from "../services/password.service";

@Injectable({
  providedIn: "root",
})
export class PasswordStore {
  private readonly passwordApi = inject(PasswordApi);

  readonly forgotSubmitting = signal(false);
  readonly forgotError = signal<AppHttpError | null>(null);
  readonly forgotSuccess = signal(false);
  readonly forgotSuccessMessage = signal<string | null>(null);
  readonly hasForgotError = computed(() => this.forgotError() !== null);

  readonly resetSubmitting = signal(false);
  readonly resetError = signal<AppHttpError | null>(null);
  readonly resetSuccess = signal(false);
  readonly resetSuccessMessage = signal<string | null>(null);
  readonly hasResetError = computed(() => this.resetError() !== null);

  readonly changeSubmitting = signal(false);
  readonly changeError = signal<AppHttpError | null>(null);
  readonly changeSuccess = signal(false);
  readonly changeSuccessMessage = signal<string | null>(null);
  readonly hasChangeError = computed(() => this.changeError() !== null);

  async forgotPassword(formValue: ForgotPasswordFormValue): Promise<boolean> {
    this.forgotSubmitting.set(true);
    this.forgotError.set(null);
    this.forgotSuccess.set(false);
    this.forgotSuccessMessage.set(null);

    try {
      const payload = toForgotPasswordRequest(formValue);

      await firstValueFrom(this.passwordApi.forgotPassword(payload));

      this.forgotSuccess.set(true);
      this.forgotSuccessMessage.set(
        "Si el correo existe, el correo de recuperacion fue enviado correctamente"
      );

      return true;
    } catch (error) {
      this.forgotError.set(error as AppHttpError);
      this.forgotSuccess.set(false);
      return false;
    } finally {
      this.forgotSubmitting.set(false);
    }
  }

  async resetPassword(formValue: ResetPasswordFormValue): Promise<boolean> {
    this.resetSubmitting.set(true);
    this.resetError.set(null);
    this.resetSuccess.set(false);
    this.resetSuccessMessage.set(null);

    try {
      const payload = toResetPasswordRequest(formValue);

      await firstValueFrom(this.passwordApi.resetPassword(payload));

      this.resetSuccess.set(true);
      this.resetSuccessMessage.set("Contraseña actualizada correctamente");

      return true;
    } catch (error) {
      this.resetError.set(error as AppHttpError);
      this.resetSuccess.set(false);
      return false;
    } finally {
      this.resetSubmitting.set(false);
    }
  }

  async changePassword(formValue: ChangePasswordFormValue): Promise<boolean> {
    this.changeSubmitting.set(true);
    this.changeError.set(null);
    this.changeSuccess.set(false);
    this.changeSuccessMessage.set(null);

    try {
      const payload = toChangePasswordRequest(formValue);

      await firstValueFrom(this.passwordApi.changePassword(payload));

      this.changeSuccess.set(true);
      this.changeSuccessMessage.set("Se cambio la Contraseña correctamente");

      return true;
    } catch (error) {
      this.changeError.set(error as AppHttpError);
      this.changeSuccess.set(false);
      return false;
    } finally {
      this.changeSubmitting.set(false);
    }
  }

  clearForgotError(): void {
    this.forgotError.set(null);
  }

  clearForgotState(): void {
    this.forgotError.set(null);
    this.forgotSuccess.set(false);
    this.forgotSuccessMessage.set(null);
  }

  clearResetError(): void {
    this.resetError.set(null);
  }

  clearResetState(): void {
    this.resetError.set(null);
    this.resetSuccess.set(false);
    this.resetSuccessMessage.set(null);
  }

  clearChangeError(): void {
    this.changeError.set(null);
  }

  clearChangeState(): void {
    this.changeError.set(null);
    this.changeSuccess.set(false);
    this.changeSuccessMessage.set(null);
  }
}
