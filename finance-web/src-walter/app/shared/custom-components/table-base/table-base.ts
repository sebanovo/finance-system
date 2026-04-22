import { NgTemplateOutlet } from "@angular/common";
import { Component, input, TemplateRef } from "@angular/core";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmTableImports } from "@shared/ui/table";

export type Column<T> = {
  key?: keyof T;
  label: string;
  template?: TemplateRef<{ $implicit: T }>;
};

@Component({
  selector: "app-table-base",
  imports: [NgTemplateOutlet, HlmTableImports, HlmBadgeImports],
  templateUrl: "./table-base.html",
  styleUrl: "./table-base.css",
})
export class TableBase<T> {
  readonly data = input<T[]>();
  readonly columns = input<Column<T>[]>();
}
