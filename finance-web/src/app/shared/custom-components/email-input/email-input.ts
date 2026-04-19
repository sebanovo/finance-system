import { Component, input } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAtSign,
  lucideBuilding,
  lucideBuilding2,
  lucideMail,
  lucideUser,
} from "@ng-icons/lucide";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";

@Component({
  selector: "app-email-input",
  imports: [ReactiveFormsModule, HlmFieldImports, HlmInputImports, NgIcon],
  providers: [
    provideIcons({
      lucideMail,
      lucideAtSign,
      lucideUser,
      lucideBuilding,
      lucideBuilding2,
    }),
  ],
  templateUrl: "./email-input.html",
  styleUrl: "./email-input.css",
})
export class EmailInput {
  readonly control = input.required<FormControl<string>>();
  readonly inputId = input.required<string>();

  readonly label = input<string>("Correo electrónico");
  readonly placeholder = input<string>("m@ejemplo.com");
  readonly description = input<string>("");
  readonly icon = input<string>("lucideMail");

  readonly requiredMessage = input<string>(
    "El correo electrónico es obligatorio."
  );
  readonly invalidMessage = input<string>(
    "Ingresa un correo electrónico válido."
  );
}
