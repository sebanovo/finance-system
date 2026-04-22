import { CommonModule } from "@angular/common";
import { Component, input, output } from "@angular/core";

import { User } from "../../models/user.model";

@Component({
  selector: "app-user-table",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./user-table.html",
  styleUrl: "./user-table.css",
})
export class UserTable {
  readonly users = input.required<User[]>();
  readonly viewUser = output<string>();

  onViewUser(userId: string): void {
    this.viewUser.emit(userId);
  }

  getFullName(user: User): string {
    return `${user.firstName} ${user.lastName}`;
  }
}
