import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucidePlus,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { UserTable } from "../../components/user-table/user-table";
import { UsersStore } from "../../store/user.store";

@Component({
  selector: "app-user-list-page",
  imports: [
    UserTable,
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucidePlus,
      lucideUsers,
    }),
  ],
  templateUrl: "./user-list-page.html",
  styleUrl: "./user-list-page.css",
})
export class UserListPage implements OnInit {
  readonly store = inject(UsersStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    void this.store.loadUsers();
  }

  onRetry(): void {
    void this.store.reloadUsers();
  }

  onCreateUser(): void {
    void this.router.navigate(["/app", "users", "create"]);
  }

  onViewUser(userId: string): void {
    void this.router.navigate(["/app", "users", userId]);
  }

  onEditUser(userId: string): void {
    void this.router.navigate(["/app", "users", userId, "edit"]);
  }

  onActivateUser(userId: string): void {
    console.log("activate user", userId);
  }

  onDeactivateUser(userId: string): void {
    console.log("deactivate user", userId);
  }
}
