import { Component, inject } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-login-page",
  imports: [],
  templateUrl: "./login-page.html",
  styleUrl: "./login-page.css",
})
export class LoginPage {
  private readonly router = inject(Router);

  async onIngresarClick(): Promise<void> {
    await this.router.navigateByUrl("/app");
  }
}
