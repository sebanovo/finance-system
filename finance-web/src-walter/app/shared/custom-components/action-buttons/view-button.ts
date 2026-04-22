import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmIcon } from "@shared/ui/icon";

@Component({
  selector: "app-view-button",
  imports: [HlmButtonImports, NgIcon, HlmIcon],
  template: `
    <button
      type="button"
      hlmBtn
      variant="outline"
      size="icon"
      class="cursor-pointer"
      [disabled]="disabled()"
      (click)="onClick()"
    >
      <ng-icon hlm size="sm" name="lucideEye" />
    </button>
  `,
  providers: [provideIcons({ lucideEye })],
})
export class ViewButton {
  readonly disabled = input(false);

  readonly clicked = output<void>();

  protected onClick(): void {
    if (this.disabled()) {
      return;
    }

    this.clicked.emit();
  }
}
