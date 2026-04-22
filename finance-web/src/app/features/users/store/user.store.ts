import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toCreateUserRequest,
  toUpdateUserRequest,
  toUser,
  toUsers,
} from "../adapters/user.adapter";
import {
  UpdateUserRequest,
  type User,
  UserUpsertFormValue,
} from "../models/user.model";
import { UsersApi } from "../services/user.service";

@Injectable({
  providedIn: "root",
})
export class UsersStore {
  private readonly usersApi = inject(UsersApi);

  readonly users = signal<User[]>([]);
  readonly loading = signal(false);
  readonly submitting = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly createError = signal<AppHttpError | null>(null);
  readonly hasLoaded = signal(false);
  readonly hasError = computed(() => this.error() !== null);
  readonly hasCreateError = computed(() => this.createError() !== null);

  readonly selectedUser = signal<User | null>(null);
  readonly selectedUserLoading = signal(false);
  readonly selectedUserError = signal<AppHttpError | null>(null);
  readonly updateError = signal<AppHttpError | null>(null);
  readonly hasSelectedUserError = computed(
    () => this.selectedUserError() !== null
  );
  readonly hasUpdateError = computed(() => this.updateError() !== null);

  // activate and deactivate signal
  readonly togglingUserIds = signal<string[]>([]);
  readonly toggleError = signal<AppHttpError | null>(null);
  readonly hasToggleError = computed(() => this.toggleError() !== null);

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.users().length === 0
  );

  async loadUsers(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const userDtos = await firstValueFrom(this.usersApi.getUsers());
      this.users.set(toUsers(userDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.users.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  async createUser(formValue: UserUpsertFormValue): Promise<User | null> {
    this.submitting.set(true);
    this.createError.set(null);

    try {
      const payload = toCreateUserRequest(formValue);
      const createdUserDto = await firstValueFrom(
        this.usersApi.createUser(payload)
      );
      const createdUser = toUser(createdUserDto);

      this.users.update(currentUsers => [createdUser, ...currentUsers]);

      return createdUser;
    } catch (error) {
      this.createError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async loadUserById(userId: string): Promise<User | null> {
    this.selectedUserLoading.set(true);
    this.selectedUserError.set(null);
    this.selectedUser.set(null);

    try {
      const userDto = await firstValueFrom(this.usersApi.getUserById(userId));
      const user = toUser(userDto);

      this.selectedUser.set(user);

      return user;
    } catch (error) {
      this.selectedUserError.set(error as AppHttpError);
      this.selectedUser.set(null);
      return null;
    } finally {
      this.selectedUserLoading.set(false);
    }
  }

  async updateUser(
    userId: string,
    formValue: UserUpsertFormValue
  ): Promise<User | null> {
    this.submitting.set(true);
    this.updateError.set(null);

    try {
      const payload: UpdateUserRequest = toUpdateUserRequest(formValue);
      const updatedUserDto = await firstValueFrom(
        this.usersApi.updateUser(userId, payload)
      );
      const updatedUser = toUser(updatedUserDto);

      this.selectedUser.set(updatedUser);

      this.users.update(currentUsers =>
        currentUsers.map(user =>
          user.id === updatedUser.id ? updatedUser : user
        )
      );

      return updatedUser;
    } catch (error) {
      this.updateError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async toggleUserActiveState(user: User): Promise<User | null> {
    this.startTogglingUser(user.id);
    this.toggleError.set(null);

    try {
      const updatedUserDto = user.isActive
        ? await firstValueFrom(this.usersApi.deactivateUser(user.id))
        : await firstValueFrom(this.usersApi.activateUser(user.id));

      const updatedUser = toUser(updatedUserDto);

      this.users.update(currentUsers =>
        currentUsers.map(currentUser =>
          currentUser.id === updatedUser.id ? updatedUser : currentUser
        )
      );

      if (this.selectedUser()?.id === updatedUser.id) {
        this.selectedUser.set(updatedUser);
      }

      return updatedUser;
    } catch (error) {
      this.toggleError.set(error as AppHttpError);
      return null;
    } finally {
      this.finishTogglingUser(user.id);
    }
  }

  isUserToggling(userId: string): boolean {
    return this.togglingUserIds().includes(userId);
  }

  private startTogglingUser(userId: string): void {
    this.togglingUserIds.update(currentIds =>
      currentIds.includes(userId) ? currentIds : [...currentIds, userId]
    );
  }

  private finishTogglingUser(userId: string): void {
    this.togglingUserIds.update(currentIds =>
      currentIds.filter(currentId => currentId !== userId)
    );
  }

  clearError(): void {
    this.error.set(null);
  }

  clearCreateError(): void {
    this.createError.set(null);
  }

  reloadUsers(): Promise<void> {
    return this.loadUsers();
  }

  clearSelectedUserError(): void {
    this.selectedUserError.set(null);
  }

  clearUpdateError(): void {
    this.updateError.set(null);
  }

  clearSelectedUser(): void {
    this.selectedUser.set(null);
  }

  clearToggleError(): void {
    this.toggleError.set(null);
  }
}
