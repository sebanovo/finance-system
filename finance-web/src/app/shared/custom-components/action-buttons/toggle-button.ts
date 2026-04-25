import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { HlmSwitchImports } from "@shared/ui/switch";

@Component({
  selector: "app-toggle-button",
  imports: [HlmSwitchImports],
  template: `
    <hlm-switch
      class="cursor-pointer"
      [checked]="checked()"
      [disabled]="disabled() || loading()"
      (checkedChange)="onCheckedChange($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToggleButton {
  readonly checked = input.required<boolean>();
  readonly disabled = input(false);
  readonly loading = input(false);

  readonly checkedChange = output<boolean>();

  protected onCheckedChange(value: boolean): void {
    if (this.disabled() || this.loading()) {
      return;
    }

    this.checkedChange.emit(value);
  }
}
