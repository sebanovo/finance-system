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
import { UserUpsertFormValue } from "../../models/user.model";

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

  readonly formSubmitted = output<UserUpsertFormValue>();
  readonly cancelClicked = output<void>();

  readonly mode = input<"create" | "edit">("create");
  readonly title = input("Nuevo usuario");
  readonly description = input(
    "Completa los datos para registrar un nuevo usuario."
  );
  readonly submitLabel = input("Guardar usuario");
  readonly initialValue = input<UserUpsertFormValue | null>(null);

  showPasswordAsReadOnly(): boolean {
    return this.mode() === "edit";
  }

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(6)]],
    firstName: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
    lastName: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
  });

  constructor() {
    effect(() => {
      const initialValue = this.initialValue();

      if (!initialValue) {
        return;
      }

      this.form.patchValue(initialValue, { emitEvent: false });

      if (this.mode() === "edit") {
        this.form.controls.password.setValue("**********", {
          emitEvent: false,
        });
        this.form.controls.password.clearValidators();
        this.form.controls.password.disable({ emitEvent: false });
        this.form.controls.password.updateValueAndValidity({
          emitEvent: false,
        });
        return;
      }

      this.form.controls.password.enable({ emitEvent: false });
      this.form.controls.password.setValidators([
        Validators.required,
        Validators.minLength(6),
      ]);
      this.form.controls.password.updateValueAndValidity({ emitEvent: false });
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

    if (fieldName === "password" && control.hasError("required")) {
      return "La contraseña es obligatoria.";
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

    if (
      (fieldName === "firstName" || fieldName === "lastName") &&
      control.hasError("minlength")
    ) {
      return "Debe contener al menos 3 caracters";
    }

    return "";
  }

  showPasswordField(): boolean {
    return this.mode() === "create";
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
