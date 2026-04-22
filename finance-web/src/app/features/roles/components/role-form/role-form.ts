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
import { RoleUpsertFormValue } from "../../model/role.type";

@Component({
  selector: "app-role-form",
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
  templateUrl: "./role-form.html",
  styleUrl: "./role-form.css",
})
export class RoleForm {
  private readonly fb = inject(FormBuilder);

  readonly submitting = input(false);
  readonly submitError = input<AppHttpError | null>(null);

  readonly formSubmitted = output<RoleUpsertFormValue>();
  readonly cancelClicked = output<void>();

  readonly mode = input<"create" | "edit">("create");
  readonly title = input("Nuevo rol");
  readonly description = input(
    "Completa los datos para registrar un nuevo rol."
  );
  readonly submitLabel = input("Guardar rol");
  readonly initialValue = input<RoleUpsertFormValue | null>(null);

  private readonly host =
    viewChild.required<ElementRef<HTMLElement>>("formRoot");

  readonly form = this.fb.nonNullable.group({
    name: [
      "",
      [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    ],
    description: [
      "",
      [Validators.required, Validators.minLength(5), Validators.maxLength(255)],
    ],
    permissionCodesText: ["", [Validators.required]],
  });

  constructor() {
    effect(() => {
      const initialValue = this.initialValue();

      if (!initialValue) {
        return;
      }

      this.form.patchValue(
        {
          name: initialValue.name,
          description: initialValue.description,
          permissionCodesText: initialValue.permissionCodes.join(", "),
        },
        { emitEvent: false }
      );
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.focusAndRevealErrors();
      return;
    }

    const rawValue = this.form.getRawValue();

    const permissionCodes = rawValue.permissionCodesText
      .split(/[\n,]/)
      .map(code => code.trim())
      .filter(Boolean);

    this.formSubmitted.emit({
      name: rawValue.name,
      description: rawValue.description,
      permissionCodes,
    });
  }

  onCancel(): void {
    this.cancelClicked.emit();
  }

  hasFieldError(
    fieldName: "name" | "description" | "permissionCodesText"
  ): boolean {
    const control = this.form.controls[fieldName];
    return control.invalid && (control.touched || control.dirty);
  }

  getFieldError(
    fieldName: "name" | "description" | "permissionCodesText"
  ): string {
    const control = this.form.controls[fieldName];

    if (control.hasError("required")) {
      return "Este campo es obligatorio.";
    }

    if (fieldName === "name" && control.hasError("minlength")) {
      return "Debe contener al menos 3 caracteres.";
    }

    if (
      (fieldName === "name" || fieldName === "description") &&
      control.hasError("maxlength")
    ) {
      return "Este campo supera la longitud permitida.";
    }

    if (fieldName === "description" && control.hasError("minlength")) {
      return "La descripción debe tener al menos 5 caracteres.";
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
