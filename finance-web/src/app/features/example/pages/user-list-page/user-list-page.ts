import { CommonModule } from "@angular/common";
import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";

import { UserForm } from "../../components/user-form/user-form";
import { UserTable } from "../../components/user-table/user-table";
import { CreateUserRequest } from "../../models/create-user-request.model";
import { UsersStore } from "../../store/users.store";

@Component({
  selector: "app-user-list-page",
  imports: [CommonModule, UserTable, UserForm],
  providers: [UsersStore],
  templateUrl: "./user-list-page.html",
  styleUrl: "./user-list-page.css",
})
export class UserListPage implements OnInit {
  // Inyecciones de dependencias
  // private readonly userService = inject(UserService);
  // private readonly router = inject(Router);

  // // Estado reactivos
  // readonly users = signal<User[]>([]);
  // readonly isLoadingUsers = signal(false);
  // readonly isCreatingUser = signal(false);
  // readonly errorMessage = signal("");
  // readonly successMessage = signal("");

  readonly store = inject(UsersStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.store.loadUsers();
  }

  onCreateUser(payload: CreateUserRequest): void {
    this.store.createUser(payload);
  }

  onViewUser(userId: string): void {
    this.router.navigate(["/users", userId]);
  }
}
