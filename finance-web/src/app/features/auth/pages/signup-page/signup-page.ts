import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { NgIcon } from "@ng-icons/core";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";

@Component({
  selector: "app-signup-page",
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    HlmInputImports,
    HlmFieldImports,
    HlmButtonImports,
    NgIcon,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./signup-page.html",
  styleUrl: "./signup-page.css",
})
export class SignupPage {
  private readonly router = inject(Router);
  private readonly _fb = inject(FormBuilder);

  form = this._fb.group(
    {
      name: ["", [Validators.required, Validators.minLength(4)]],
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8)]],
      confirmPassword: ["", [Validators.required]],
    },
    { validators: passwordMatch() }
  );

  signup() {
    if (this.form.valid) {
      // signup logic here
      console.log(this.form.value);
      this.router.navigateByUrl("/app");
    }
  }
}

function passwordMatch(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get("password")?.value;
    const confirm = group.get("confirmPassword")?.value;
    return password === confirm ? null : { passwordMismatch: true };
  };
}
