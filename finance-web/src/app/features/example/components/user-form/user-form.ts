import { CommonModule } from "@angular/common";
import { Component, inject, output } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { CreateUserRequest } from "../../models/create-user-request.model";

@Component({
  selector: "app-user-form",
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./user-form.html",
  styleUrl: "./user-form.css",
})
export class UserForm {
  // forma moderna en vez de constructor
  private readonly fb = inject(FormBuilder);

  // Crea un Evento que el padre pueda escuchar
  readonly saveUser = output<CreateUserRequest>();

  // nonNullable = evita valores nulos
  readonly form = this.fb.nonNullable.group({
    firstName: ["", [Validators.required, Validators.maxLength(100)]],
    lastName: ["", [Validators.required, Validators.maxLength(100)]],
    email: ["", [Validators.required, Validators.email]],
    roleId: ["", [Validators.required]],
    tenantSlug: ["", [Validators.required, Validators.maxLength(100)]],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // Emite los datos al componente padre
    // getRawValue = obtiene valores incluyendo deshabilitados
    this.saveUser.emit(this.form.getRawValue());
    // reset del formulario
    this.form.reset({
      firstName: "",
      lastName: "",
      email: "",
      roleId: "",
    });
  }
}
