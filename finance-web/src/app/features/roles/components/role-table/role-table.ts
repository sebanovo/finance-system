import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye, lucidePencil } from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSwitchImports } from "@shared/ui/switch";
import { HlmTableImports } from "@shared/ui/table";
import { Role } from "../../model/role.type";

@Component({
  selector: "app-role-table",
  imports: [
    HlmTableImports,
    HlmBadgeImports,
    HlmButtonImports,
    NgIcon,
    HlmSwitchImports,
  ],
  providers: [provideIcons({ lucidePencil, lucideEye })],
  templateUrl: "./role-table.html",
  styleUrl: "./role-table.css",
})
export class RoleTable {
  readonly roles = input.required<Role[]>();

  readonly viewRole = output<Role>();
  readonly activateRole = output<Role>();
  readonly deactivateRole = output<Role>();
  readonly editRole = output<string>();

  readonly togglingRoleIds = input<string[]>([]);

  onViewRole(role: Role): void {
    console.log("click en onViewRole", role);
    this.viewRole.emit(role);
  }

  onEditRole(roleId: string): void {
    this.editRole.emit(roleId);
  }

  isRoleToggling(roleId: string): boolean {
    return this.togglingRoleIds().includes(roleId);
  }

  onToggleRole(role: Role, value: boolean): void {
    if (value) {
      this.deactivateRole.emit(role);
    } else {
      this.activateRole.emit(role);
    }
  }
}
