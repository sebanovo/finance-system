import { Component, inject } from "@angular/core";
import { Router } from "@angular/router";
import { toast } from "@spartan-ng/brain/sonner";
import { ForgotPasswordForm } from "../../components/forgot-password-form/forgot-password-form";
import { PasswordStore } from "../../store/password.store";

@Component({
  selector: "app-forgot-password-page",
  imports: [ForgotPasswordForm],
  templateUrl: "./forgot-password-page.html",
  styleUrl: "./forgot-password-page.css",
})
export class ForgotPasswordPage {
  private readonly router = inject(Router);
  private readonly passwordStore = inject(PasswordStore);

  async onSuccess(): Promise<void> {
    toast.success("Correo de recuperación enviado", {
      description:
        this.passwordStore.forgotSuccessMessage() ??
        "Revisa tu bandeja de entrada para continuar.",
      duration: 8000,
    });

    await this.router.navigate(["/auth", "login"]);
  }
}
