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
import { lucideAlertCircle, lucideLoaderCircle } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { AppHttpError } from "../../../../core/http/models/app-http-error.model";
import { TenantSettingsFormValue } from "../../models/setting.type";

@Component({
  selector: "app-setting-form",
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
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./setting-form.html",
  styleUrl: "./setting-form.css",
})
export class SettingForm {
  private readonly fb = inject(FormBuilder);

  readonly submitting = input(false);
  readonly submitError = input<AppHttpError | null>(null);
  readonly initialValue = input<TenantSettingsFormValue | null>(null);

  readonly formSubmitted = output<TenantSettingsFormValue>();

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    companyName: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(120)],
    ],
    legalName: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(150)],
    ],
    timezone: ["", [Validators.required, Validators.maxLength(80)]],
    currency: ["", [Validators.required, Validators.maxLength(20)]],
    contactEmail: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
    contactPhone: [
      "",
      [Validators.required, Validators.minLength(5), Validators.maxLength(30)],
    ],
  });

  constructor() {
    effect(() => {
      const initialValue = this.initialValue();

      if (!initialValue) {
        return;
      }

      this.form.patchValue(initialValue, { emitEvent: false });
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.focusAndRevealErrors();
      return;
    }

    this.formSubmitted.emit(this.form.getRawValue());
  }

  hasFieldError(fieldName: keyof TenantSettingsFormValue): boolean {
    const control = this.form.controls[fieldName];
    return control.invalid && (control.touched || control.dirty);
  }

  getFieldError(fieldName: keyof TenantSettingsFormValue): string {
    const control = this.form.controls[fieldName];

    if (control.hasError("required")) {
      return "Este campo es obligatorio.";
    }

    if (fieldName === "contactEmail" && control.hasError("email")) {
      return "Ingresa un correo válido.";
    }

    if (
      (fieldName === "companyName" || fieldName === "legalName") &&
      control.hasError("minlength")
    ) {
      return "Debe contener al menos 3 caracteres.";
    }

    if (fieldName === "contactPhone" && control.hasError("minlength")) {
      return "Debe contener al menos 5 caracteres.";
    }

    if (control.hasError("maxlength")) {
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
