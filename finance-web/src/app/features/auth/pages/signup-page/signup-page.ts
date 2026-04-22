import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { Router } from "@angular/router";
import { SignupForm } from "../../components/signup-form/signup-form";
import { SignupRequest } from "../../models/signup-request.type";
import { AuthStore } from "../../store/auth.store";
import { toast } from "@spartan-ng/brain/sonner";

@Component({
  selector: "app-signup-page",
  standalone: true,
  imports: [SignupForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./signup-page.html",
  styleUrl: "./signup-page.css",
})
export class SignupPage {
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly isSubmitting = this.authStore.loading;
  readonly errorMessage = this.authStore.errorMessage;

  async onSubmit(payload: SignupRequest): Promise<void> {
    const success = await this.authStore.signup(payload);

    if (!success) {
      toast("Error en el registro", {
        description:
          this.authStore.errorMessage() ||
          "No se pudo crear la cuenta. Intenta nuevamente.",
      });
      return;
    }

    toast("¡Bienvenido!", {
      description: `Tu tenant "${payload.tenantSlug}" ha sido creado exitosamente. Redirigiendo al dashboard...`,
    });

    // Redirect to app dashboard after successful signup
    setTimeout(() => {
      void this.router.navigateByUrl("/app/dashboard");
    }, 1500);
  }

  onFormEdited(): void {
    this.authStore.clearError();
  }
}
