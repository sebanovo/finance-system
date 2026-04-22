import { Component, input, output } from "@angular/core";
import { NgIcon } from "@ng-icons/core";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";

@Component({
  selector: "app-card-header",
  imports: [HlmCardImports, HlmButtonImports, NgIcon],
  templateUrl: "./card-header.html",
  styleUrl: "./card-header.css",
})
export class CardHeader {
  // Header
  readonly title = input.required<string>();
  readonly description = input<string>();
  readonly icon = input<string>();

  // Action
  readonly showAction = input(false);
  readonly actionLabel = input("Nuevo");
  readonly actionIcon = input<string>();

  readonly actionClick = output<void>();
}
