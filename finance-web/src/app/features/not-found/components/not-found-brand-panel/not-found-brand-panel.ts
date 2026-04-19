import { Component, input } from "@angular/core";
import { NgIcon } from "@ng-icons/core";

@Component({
  selector: "app-not-found-brand-panel",
  imports: [NgIcon],
  templateUrl: "./not-found-brand-panel.html",
  styleUrl: "./not-found-brand-panel.css",
})
export class NotFoundBrandPanel {
  readonly eyebrow = input.required<string>();
  readonly title = input.required<string>();
  readonly description = input.required<string>();

  readonly infoTitle = input.required<string>();
  readonly infoDescription = input.required<string>();

  readonly boxTitle = input<string>("Identidad visual");
  readonly boxDescription = input<string>(
    "Preparado para colocar el logo de nuestra empresa, una imagen institucional o una composición visual del sistema."
  );

  readonly icon = input<string>();
  readonly companyName = input<string>("Thunder Company");
}
