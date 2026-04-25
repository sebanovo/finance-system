import { Component, input, signal } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideCheck,
  lucideEye,
  lucideEyeOff,
  lucideKey,
  lucideLock,
} from "@ng-icons/lucide";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";

@Component({
  selector: "app-password-input",
  imports: [ReactiveFormsModule, HlmFieldImports, HlmInputImports, NgIcon],
  providers: [
    provideIcons({
      lucideLock,
      lucideEye,
      lucideEyeOff,
      lucideCheck,
      lucideKey,
    }),
  ],
  templateUrl: "./password-input.html",
  styleUrl: "./password-input.css",
})
export class PasswordInput {
  readonly control = input.required<FormControl<string>>();
  readonly inputId = input.required<string>();

  readonly label = input<string>("Contraseña");
  readonly placeholder = input<string>("********");
  readonly description = input<string>("");
  readonly requiredMessage = input<string>("Este campo es obligatorio.");
  readonly minLengthMessage = input<string>(
    "Debe tener al menos 8 caracteres."
  );

  readonly icon = input<string>("lucideLock");

  readonly showPassword = signal(false);

  togglePassword(): void {
    this.showPassword.update(value => !value);
  }
}
