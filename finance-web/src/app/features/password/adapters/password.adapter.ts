import {
  ChangePasswordFormValue,
  ChangePasswordRequest,
  ForgotPasswordFormValue,
  ForgotPasswordRequest,
  ResetPasswordFormValue,
  ResetPasswordRequest,
} from "../model/password.type";

export function toForgotPasswordRequest(
  formValue: ForgotPasswordFormValue
): ForgotPasswordRequest {
  return {
    email: formValue.email.trim(),
  };
}

export function toResetPasswordRequest(
  formValue: ResetPasswordFormValue
): ResetPasswordRequest {
  return {
    token: formValue.token.trim(),
    newPassword: formValue.newPassword.trim(),
  };
}

export function toChangePasswordRequest(
  formValue: ChangePasswordFormValue
): ChangePasswordRequest {
  return {
    currentPassword: formValue.currentPassword.trim(),
    newPassword: formValue.newPassword.trim(),
  };
}
