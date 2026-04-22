import { inject, Injectable, signal } from "@angular/core";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { CreateUserRequest } from "../models/create-user-request.model";
import { User } from "../models/user.model";
import { UserService } from "../services/user.service";

@Injectable()
export class UsersStore {
  private readonly userService = inject(UserService);

  readonly users = signal<User[]>([]);
  readonly isLoadingUsers = signal(false);
  readonly isCreatingUser = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly successMessage = signal<string | null>(null);

  loadUsers(): void {
    this.isLoadingUsers.set(true);
    this.error.set(null);

    this.userService.getUsers().subscribe({
      next: users => {
        this.users.set(users);
        this.isLoadingUsers.set(false);
      },
      error: (error: AppHttpError) => {
        this.error.set(error);
        this.isLoadingUsers.set(false);
      },
    });
  }

  createUser(payload: CreateUserRequest): void {
    this.isCreatingUser.set(true);
    this.error.set(null);
    this.successMessage.set(null);

    this.userService.createUser(payload).subscribe({
      next: createdUser => {
        this.users.update(currentUsers => [createdUser, ...currentUsers]);
        this.successMessage.set("User created successfully");
        this.isCreatingUser.set(false);
      },
      error: (error: AppHttpError) => {
        this.error.set(error);
        this.isCreatingUser.set(false);
      },
    });
  }

  clearMessages(): void {
    this.error.set(null);
    this.successMessage.set(null);
  }
}
