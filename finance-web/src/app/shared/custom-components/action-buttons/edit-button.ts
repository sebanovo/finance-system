import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucidePencil } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmIcon } from "@shared/ui/icon";

@Component({
  selector: "app-edit-button",
  imports: [HlmButtonImports, NgIcon, HlmIcon],
  providers: [provideIcons({ lucidePencil })],
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
      <ng-icon hlm size="sm" name="lucidePencil" />
    </button>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditButton {
  readonly disabled = input(false);

  readonly clicked = output<void>();

  protected onClick(): void {
    if (this.disabled()) {
      return;
    }

    this.clicked.emit();
  }
}
