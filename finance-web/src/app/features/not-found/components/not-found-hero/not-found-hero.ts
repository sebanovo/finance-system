import { Component, input } from "@angular/core";
import { NgIcon } from "@ng-icons/core";
import { HlmCardImports } from "@shared/ui/card";

@Component({
  selector: "app-not-found-hero",
  imports: [NgIcon, HlmCardImports],
  templateUrl: "./not-found-hero.html",
  styleUrl: "./not-found-hero.css",
})
export class NotFoundHero {
  readonly icon = input.required<string>();
  readonly eyebrow = input.required<string>();
  readonly title = input.required<string>();
  readonly description = input.required<string>();
}
