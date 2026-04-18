import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideTrash2 } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmIcon } from "@shared/ui/icon";

@Component({
  selector: "app-delete-button",
  imports: [HlmButtonImports, NgIcon, HlmIcon],
  providers: [provideIcons({ lucideTrash2 })],
  template: `
    <button
      type="button"
      hlmBtn
      variant="destructive"
      size="icon"
      class="cursor-pointer"
      [disabled]="disabled()"
      (click)="onClick()"
    >
      <ng-icon hlm size="sm" name="lucideTrash2" />
    </button>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteButton {
  readonly disabled = input(false);

  readonly clicked = output<void>();

  protected onClick(): void {
    if (this.disabled()) {
      return;
    }

    this.clicked.emit();
  }
}
