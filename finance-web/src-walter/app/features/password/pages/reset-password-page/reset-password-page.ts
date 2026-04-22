import { Component, inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { toast } from "@spartan-ng/brain/sonner";
import { ResetPasswordForm } from "../../components/reset-password-form/reset-password-form";

@Component({
  selector: "app-reset-password-page",
  imports: [ResetPasswordForm],
  templateUrl: "./reset-password-page.html",
  styleUrl: "./reset-password-page.css",
})
export class ResetPasswordPage {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly initialToken = this.route.snapshot.queryParamMap.get("token") ?? "";

  async onSuccess(): Promise<void> {
    toast.success("Contraseña actualizada", {
      description: "Tu contraseña fue actualizada correctamente.",
    });

    await this.router.navigate(["/auth", "login"]);
  }
}
