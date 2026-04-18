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
import { TenantUpsertFormValue } from "../../models/tenant.type";

@Component({
  selector: "app-tenant-form",
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
  templateUrl: "./tenant-form.html",
  styleUrl: "./tenant-form.css",
})
export class TenantForm {
  private readonly fb = inject(FormBuilder);

  readonly submitting = input(false);
  readonly submitError = input<AppHttpError | null>(null);

  readonly formSubmitted = output<TenantUpsertFormValue>();
  readonly cancelClicked = output<void>();

  readonly mode = input<"create" | "edit">("create");
  readonly title = input("Nuevo tenant");
  readonly description = input(
    "Completa los datos para registrar un nuevo tenant."
  );
  readonly submitLabel = input("Guardar tenant");
  readonly initialValue = input<TenantUpsertFormValue | null>(null);

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    name: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
    slug: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
    planCode: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
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

  onCancel(): void {
    this.cancelClicked.emit();
  }

  hasFieldError(fieldName: keyof TenantUpsertFormValue): boolean {
    const control = this.form.controls[fieldName];
    return control.invalid && (control.touched || control.dirty);
  }

  getFieldError(fieldName: keyof TenantUpsertFormValue): string {
    const control = this.form.controls[fieldName];

    if (control.hasError("required")) {
      return "Este campo es obligatorio.";
    }

    if (
      (fieldName === "name" || fieldName === "slug") &&
      control.hasError("minlength")
    ) {
      return "Debe contener al menos 3 caracteres.";
    }

    if (
      (fieldName === "name" ||
        fieldName === "slug" ||
        fieldName === "planCode") &&
      control.hasError("maxlength")
    ) {
      return "Este campo supera la longitud permitida.";
    }

    if (fieldName === "planCode" && control.hasError("minlength")) {
      return "Debe contener al menos 2 caracteres.";
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
