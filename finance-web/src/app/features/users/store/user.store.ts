import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";

import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { toCreateUserRequest, toUser, toUsers } from "../adapters/user.adapter";
import { UserFormValue } from "../models/user-request.type";
import { type User } from "../models/user.model";
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

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.users().length === 0
  );

  readonly hasError = computed(() => this.error() !== null);
  readonly hasCreateError = computed(() => this.createError() !== null);

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

  async createUser(formValue: UserFormValue): Promise<User | null> {
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

  clearError(): void {
    this.error.set(null);
  }

  clearCreateError(): void {
    this.createError.set(null);
  }

  reloadUsers(): Promise<void> {
    return this.loadUsers();
  }
}
