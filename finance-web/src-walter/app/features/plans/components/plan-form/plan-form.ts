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
import { PlanUpsertFormValue } from "../../models/plan.type";

@Component({
  selector: "app-plan-form",
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
  templateUrl: "./plan-form.html",
  styleUrl: "./plan-form.css",
})
export class PlanForm {
  private readonly fb = inject(FormBuilder);

  readonly submitting = input(false);
  readonly submitError = input<AppHttpError | null>(null);

  readonly formSubmitted = output<PlanUpsertFormValue>();
  readonly cancelClicked = output<void>();

  readonly mode = input<"create" | "edit">("create");
  readonly title = input("Nuevo plan");
  readonly description = input(
    "Completa los datos para registrar un nuevo plan."
  );
  readonly submitLabel = input("Guardar plan");
  readonly initialValue = input<PlanUpsertFormValue | null>(null);

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    code: [
      "",
      [Validators.required, Validators.minLength(2), Validators.maxLength(50)],
    ],
    name: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
    description: [
      "",
      [Validators.required, Validators.minLength(5), Validators.maxLength(255)],
    ],
    maxUsers: [1, [Validators.required, Validators.min(1)]],
    maxRoles: [1, [Validators.required, Validators.min(1)]],
    planType: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
    ],
    trialDays: [0, [Validators.required, Validators.min(0)]],
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

  hasFieldError(fieldName: keyof PlanUpsertFormValue): boolean {
    const control = this.form.controls[fieldName];
    return control.invalid && (control.touched || control.dirty);
  }

  getFieldError(fieldName: keyof PlanUpsertFormValue): string {
    const control = this.form.controls[fieldName];

    if (control.hasError("required")) {
      return "Este campo es obligatorio.";
    }

    if (
      (fieldName === "code" ||
        fieldName === "name" ||
        fieldName === "planType") &&
      control.hasError("minlength")
    ) {
      return "Debe contener la longitud mínima requerida.";
    }

    if (
      (fieldName === "code" ||
        fieldName === "name" ||
        fieldName === "planType" ||
        fieldName === "description") &&
      control.hasError("maxlength")
    ) {
      return "Este campo supera la longitud permitida.";
    }

    if (fieldName === "description" && control.hasError("minlength")) {
      return "La descripción debe tener al menos 5 caracteres.";
    }

    if (
      (fieldName === "maxUsers" ||
        fieldName === "maxRoles" ||
        fieldName === "trialDays") &&
      control.hasError("min")
    ) {
      return "El valor ingresado no es válido.";
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
