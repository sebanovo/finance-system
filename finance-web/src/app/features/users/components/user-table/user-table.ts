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

  readonly viewUser = output<string>();
  readonly editUser = output<string>();
  readonly activateUser = output<string>();
  readonly deactivateUser = output<string>();

  onViewUser(userId: string): void {
    this.viewUser.emit(userId);
  }

  onEditUser(userId: string): void {
    this.editUser.emit(userId);
  }

  onActivateUser(userId: string): void {
    this.activateUser.emit(userId);
  }

  onDeactivateUser(userId: string): void {
    this.deactivateUser.emit(userId);
  }
}
