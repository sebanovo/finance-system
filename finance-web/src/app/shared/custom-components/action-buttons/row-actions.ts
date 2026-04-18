import { ChangeDetectionStrategy, Component } from "@angular/core";

@Component({
  selector: "app-row-actions",
  imports: [],
  host: {
    class: "block",
  },
  template: `
    <div class="flex items-center gap-2">
      <ng-content />
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RowActions {}
