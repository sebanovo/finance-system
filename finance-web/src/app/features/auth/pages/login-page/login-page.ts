import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { remixGithubFill } from "@ng-icons/remixicon";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";

@Component({
  selector: "app-login-page",
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmFieldImports,
    HlmInputImports,
    HlmButtonImports,
    NgIcon,
  ],
  providers: [provideIcons({ remixGithubFill })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-page.html",
  styleUrl: "./login-page.css",
})
export class LoginPage {
  private readonly router = inject(Router);

  private readonly _fb = inject(FormBuilder);

  form = this._fb.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(8)]],
  });

  login() {
    if (this.form.valid) {
      // login logic here
      console.log(this.form.value);
      this.router.navigateByUrl("/app");
    }
  }
}
