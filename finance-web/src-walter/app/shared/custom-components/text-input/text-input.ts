import { Component, input } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideBuilding2, lucideStore, lucideUser } from "@ng-icons/lucide";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";

@Component({
  selector: "app-text-input",
  imports: [ReactiveFormsModule, HlmFieldImports, HlmInputImports, NgIcon],
  providers: [provideIcons({ lucideUser, lucideBuilding2, lucideStore })],
  templateUrl: "./text-input.html",
  styleUrl: "./text-input.css",
})
export class TextInput {
  readonly control = input.required<FormControl<string>>();
  readonly inputId = input.required<string>();

  readonly label = input<string>("");
  readonly placeholder = input<string>("");
  readonly description = input<string>("");

  readonly icon = input<string | null>(null);

  // mensajes configurables
  readonly requiredMessage = input<string>("Este campo es obligatorio.");
  readonly minLengthMessage = input<string>(
    "Debe contener minimo 2 caracteres"
  );
}
