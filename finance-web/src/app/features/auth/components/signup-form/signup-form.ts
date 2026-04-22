import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    inject,
    input,
    output,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import {
    AbstractControl,
    FormBuilder,
    ReactiveFormsModule,
    ValidationErrors,
    ValidatorFn,
    Validators,
} from "@angular/forms";
import { RouterLink } from "@angular/router";

import { CommonModule } from "@angular/common";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { SignupRequest } from "../../models/signup-request.type";

@Component({
    selector: "app-signup-form",
    imports: [
        ReactiveFormsModule,
        CommonModule,
        RouterLink,
        HlmFieldImports,
        HlmInputImports,
        HlmButtonImports,
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: "./signup-form.html",
    styleUrl: "./signup-form.css",
})
export class SignupForm {
    private readonly fb = inject(FormBuilder);
    private readonly destroyRef = inject(DestroyRef);

    readonly isSubmitting = input(false);
    readonly errorMessage = input<string | null>(null);
    readonly submitSignup = output<SignupRequest>();
    readonly formEdited = output<void>();

    readonly form = this.fb.nonNullable.group(
        {
            companyName: ["", [Validators.required, Validators.minLength(3)]],
            tenantSlug: ["", [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-z0-9-]+$/)]],
            adminEmail: ["", [Validators.required, Validators.email]],
            firstName: ["", [Validators.required, Validators.minLength(2)]],
            lastName: ["", [Validators.required, Validators.minLength(2)]],
            password: ["", [Validators.required, Validators.minLength(8)]],
            confirmPassword: ["", [Validators.required]],
        },
        { validators: passwordMatch() }
    );

    constructor() {
        this.form.valueChanges
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
                this.formEdited.emit();
            });
    }

    submit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        const rawValue = this.form.getRawValue();

        const payload: SignupRequest = {
            companyName: rawValue.companyName.trim(),
            tenantSlug: rawValue.tenantSlug.trim().toLowerCase(),
            adminEmail: rawValue.adminEmail.trim().toLowerCase(),
            firstName: rawValue.firstName.trim(),
            lastName: rawValue.lastName.trim(),
            password: rawValue.password,
        };

        this.submitSignup.emit(payload);
    }

    getFieldError(fieldName: string): string | null {
        const field = this.form.get(fieldName);

        if (!field || !field.errors || !field.touched) {
            return null;
        }

        if (field.errors["required"]) {
            return "Este campo es requerido";
        }

        if (field.errors["minlength"]) {
            const min = field.errors["minlength"].requiredLength;
            return `Mínimo ${min} caracteres`;
        }

        if (field.errors["email"]) {
            return "Email inválido";
        }

        if (field.errors["pattern"]) {
            return "Solo letras, números y guiones permitidos";
        }

        if (field.errors["passwordMismatch"]) {
            return "Las contraseñas no coinciden";
        }

        return "Campo inválido";
    }

    hasFieldError(fieldName: string): boolean {
        const field = this.form.get(fieldName);
        return !!(field && field.errors && field.touched);
    }
}

function passwordMatch(): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
        const password = group.get("password")?.value;
        const confirm = group.get("confirmPassword")?.value;
        return password === confirm ? null : { passwordMismatch: true };
    };
}
