import type { FormControl, FormGroup } from "@angular/forms";

export type ForgotPasswordRequest = {
  email: string;
};

export type ResetPasswordRequest = {
  token: string;
  newPassword: string;
};

export type ChangePasswordRequest = {
  currentPassword: string;
  newPassword: string;
};

export type ForgotPasswordFormValue = {
  email: string;
};

export type ResetPasswordFormValue = {
  token: string;
  newPassword: string;
  confirmNewPassword: string;
};

export type ChangePasswordFormValue = {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
};

export type PasswordActionData = Record<string, string>;

export type PasswordResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};

export type ForgotPasswordResponse = PasswordResponse<PasswordActionData>;
export type ResetPasswordResponse = PasswordResponse<PasswordActionData>;
export type ChangePasswordResponse = PasswordResponse<PasswordActionData>;

export type ForgotPasswordForm = FormGroup<{
  email: FormControl<string>;
}>;

export type ResetPasswordForm = FormGroup<{
  token: FormControl<string>;
  newPassword: FormControl<string>;
  confirmNewPassword: FormControl<string>;
}>;

export type ChangePasswordForm = FormGroup<{
  currentPassword: FormControl<string>;
  newPassword: FormControl<string>;
  confirmNewPassword: FormControl<string>;
}>;
