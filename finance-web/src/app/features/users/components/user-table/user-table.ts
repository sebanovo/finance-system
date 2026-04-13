import {
  ChangeDetectionStrategy,
  Component,
  input,
  output,
} from "@angular/core";
import { HlmBadgeImports } from "@shared/ui/badge";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmTableImports } from "@shared/ui/table";
import { User } from "../../models/user.model";

@Component({
  selector: "app-user-table",
  imports: [HlmTableImports, HlmBadgeImports, HlmButtonImports],
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

  onViewUser(user: User): void {
    console.log("click en onViewUser", user);
    this.viewUser.emit(user);
  }

  onEditUser(userId: string): void {
    this.editUser.emit(userId);
  }

  onActivateUser(user: User): void {
    this.activateUser.emit(user);
  }

  onDeactivateUser(user: User): void {
    this.deactivateUser.emit(user);
  }

  isUserToggling(userId: string): boolean {
    return this.togglingUserIds().includes(userId);
  }
}
