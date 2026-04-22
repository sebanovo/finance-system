import { DatePipe } from "@angular/common";
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
} from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import { RouterLink } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideCheck,
  lucideCreditCard,
  lucideInfo,
  lucideLock,
  lucideMail,
  lucideSearch,
  lucideStar,
} from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmDialogImports } from "@shared/ui/dialog";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { HlmInputGroupImports } from "@shared/ui/input-group";
import { HlmPopoverImports } from "@shared/ui/popover";
import { AuthStore } from "../../store/auth.store";

@Component({
  selector: "app-signup-page",
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DatePipe,
    RouterLink,
    NgIcon,
    HlmInputImports,
    HlmFieldImports,
    HlmButtonImports,
    HlmCardImports,
    HlmBadgeImports,
    HlmInputGroupImports,
    HlmPopoverImports,
    HlmDialogImports,
  ],
  providers: [
    provideIcons({
      lucideSearch,
      lucideMail,
      lucideCheck,
      lucideCreditCard,
      lucideStar,
      lucideInfo,
      lucideLock,
    }),
  ],
  host: {
    class: "grid w-full max-w-sm gap-6",
  },
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./signup-page.html",
  styleUrl: "./signup-page.css",
})
export class SignupPage {
  private readonly fb = inject(FormBuilder);
  readonly authStore = inject(AuthStore);

  readonly form = this.fb.group(
    {
      adminEmail: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8)]],
      confirmPassword: ["", [Validators.required]],
      firstName: ["", [Validators.required, Validators.minLength(2)]],
      lastName: ["", [Validators.required, Validators.minLength(2)]],
      companyName: ["", [Validators.required, Validators.minLength(3)]],
      tenantSlug: [
        "",
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50),
          Validators.pattern(/^[a-z0-9]+(?:-[a-z0-9]+)*$/),
        ],
      ],
    },
    { validators: passwordMatch() }
  );

  readonly signupResult = this.authStore.signupResult;

  readonly successDialogOpen = computed(() => this.signupResult() !== null);

  readonly friendlyLoginHint = computed(() => {
    const result = this.signupResult();
    if (!result) {
      return null;
    }

    return [
      "Ya puedes iniciar sesión con los datos que acabas de registrar.",
      `En el campo de empresa, alias o espacio de trabajo debes escribir: ${result.tenantSlug}`,
      `Tu correo administrador es: ${result.adminEmail}`,
    ];
  });

  async signup(): Promise<void> {
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      return;
    }

    const {
      companyName,
      tenantSlug,
      firstName,
      lastName,
      adminEmail,
      password,
    } = this.form.getRawValue();

    await this.authStore.signup({
      companyName: companyName ?? "",
      tenantSlug: tenantSlug ?? "",
      firstName: firstName ?? "",
      lastName: lastName ?? "",
      adminEmail: adminEmail ?? "",
      password: password ?? "",
    });
  }

  closeSuccessDialog(): void {
    this.authStore.clearSignupResult();
  }
}

function passwordMatch(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get("password")?.value;
    const confirm = group.get("confirmPassword")?.value;

    return password === confirm ? null : { passwordMismatch: true };
  };
}
