import {
  Component,
  effect,
  ElementRef,
  inject,
  input,
  output,
  viewChild,
} from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideLoaderCircle,
  lucideLock,
  lucideMail,
  lucideUser,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { AppHttpError } from "../../../../core/http/models/app-http-error.model";
import { UserFormValue } from "../../models/user-request.type";

@Component({
  selector: "app-user-form",
  imports: [
    ReactiveFormsModule,
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmFieldImports,
    HlmInputImports,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideLoaderCircle,
      lucideMail,
      lucideLock,
      lucideUser,
    }),
  ],
  templateUrl: "./user-form.html",
  styleUrl: "./user-form.css",
})
export class UserForm {
  private readonly fb = inject(FormBuilder);

  readonly submitting = input(false);
  readonly submitError = input<AppHttpError | null>(null);

  readonly formSubmitted = output<UserFormValue>();
  readonly cancelClicked = output<void>();

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(6)]],
    firstName: ["", [Validators.required, Validators.maxLength(100)]],
    lastName: ["", [Validators.required, Validators.maxLength(100)]],
  });

  constructor() {
    effect(() => {
      if (this.submitError()) {
        queueMicrotask(() => this.focusFirstInvalidField());
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.focusAndRevealErrors();
      return;
    }

    this.formSubmitted.emit(this.form.getRawValue());
  }

  onCancel(): void {
    this.cancelClicked.emit();
  }

  hasFieldError(fieldName: keyof UserFormValue): boolean {
    const control = this.form.controls[fieldName];
    return control.invalid && (control.touched || control.dirty);
  }

  getFieldError(fieldName: keyof UserFormValue): string {
    const control = this.form.controls[fieldName];

    if (control.hasError("required")) {
      return "Este campo es obligatorio.";
    }

    if (fieldName === "email" && control.hasError("email")) {
      return "Ingresa un correo válido.";
    }

    if (fieldName === "password" && control.hasError("minlength")) {
      return "La contraseña debe tener al menos 6 caracteres.";
    }

    if (
      (fieldName === "firstName" || fieldName === "lastName") &&
      control.hasError("maxlength")
    ) {
      return "Este campo supera la longitud permitida.";
    }

    return "";
  }

  private focusFirstInvalidField(): void {
    const firstInvalidField =
      this.host().nativeElement.querySelector<HTMLElement>(
        "input.ng-invalid, textarea.ng-invalid, select.ng-invalid"
      );

    firstInvalidField?.focus();
  }

  private focusAndRevealErrors(): void {
    this.form.markAllAsTouched();
    queueMicrotask(() => this.focusFirstInvalidField());
  }
}
