import { Component, input } from "@angular/core";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmTableImports } from "@shared/ui/table";
import { Permission } from "../../model/permission.type";

@Component({
  selector: "app-permission-table",
  imports: [HlmTableImports, HlmBadgeImports],
  templateUrl: "./permission-table.html",
  styleUrl: "./permission-table.css",
})
export class PermissionTable {
  readonly permissions = input.required<Permission[]>();
}
