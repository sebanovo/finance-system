import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideAlertCircle } from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";

@Component({
  selector: "app-table-error",
  imports: [HlmAlertImports, HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucideAlertCircle })],
  templateUrl: "./table-error.html",
  styleUrl: "./table-error.css",
})
export class TableError {
  // contenido
  readonly title = input.required<string>();
  readonly description = input<string>();

  // icono
  readonly icon = input<string>("lucideAlertCircle");
  readonly sizeIcon = input<string>("40");

  // estilo (spartan)
  readonly variant = input<"default" | "destructive">();

  // acción
  readonly showAction = input(false);
  readonly actionLabel = input("Reintentar");

  readonly actionClick = output<void>();
}
