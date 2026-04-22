import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideInbox } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmEmptyImports } from "@shared/ui/empty";

@Component({
  selector: "app-empty-state",
  imports: [NgIcon, HlmEmptyImports, HlmButtonImports],
  providers: [provideIcons({ lucideInbox })],
  templateUrl: "./empty-state.html",
  styleUrl: "./empty-state.css",
})
export class EmptyState {
  readonly title = input.required<string>();
  readonly description = input<string>();

  // icono
  readonly icon = input<string>("lucideInbox");

  // acción
  readonly showAction = input(false);
  readonly actionLabel = input("Crear");
  readonly actionIcon = input<string>("lucidePlus");

  readonly actionClick = output<void>();
}
