import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye, lucidePencil, lucideShield } from "@ng-icons/lucide";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSwitchImports } from "@shared/ui/switch";
import { HlmTableImports } from "@shared/ui/table";
import { User } from "../../models/user.model";

@Component({
  selector: "app-user-table",
  imports: [
    HlmTableImports,
    HlmBadgeImports,
    HlmButtonImports,
    NgIcon,
    HlmSwitchImports,
  ],
  providers: [provideIcons({ lucidePencil, lucideEye, lucideShield })],
  templateUrl: "./user-table.html",
  styleUrl: "./user-table.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserTable {
  readonly users = input.required<User[]>();

  readonly viewUser = output<User>();
  readonly activateUser = output<User>();
  readonly deactivateUser = output<User>();
  readonly editUser = output<string>();

  readonly togglingUserIds = input<string[]>([]);

  readonly manageRoles = output<User>();

  onViewUser(user: User): void {
    console.log("click en onViewUser", user);
    this.viewUser.emit(user);
  }

  onEditUser(userId: string): void {
    this.editUser.emit(userId);
  }

  isUserToggling(userId: string): boolean {
    return this.togglingUserIds().includes(userId);
  }

  onToggleUser(user: User, value: boolean) {
    if (value) {
      this.deactivateUser.emit(user);
    } else {
      this.activateUser.emit(user);
    }
  }

  onManageRoles(user: User): void {
    this.manageRoles.emit(user);
  }
}
