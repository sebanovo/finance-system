import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import { RolesStore } from "../../roles/store/role.store";
import {
  toAssignUserRolesRequest,
  toUserRoleAssignment,
} from "../adapters/role-assignment.adapter";
import {
  UserRoleAssignment,
  UserRoleAssignmentFormValue,
} from "../model/role-assignment.type";
import { UserRoleAssignmentApi } from "../services/role-assignment.service";

@Injectable({
  providedIn: "root",
})
export class UserRoleAssignmentStore {
  private readonly userRoleAssignmentApi = inject(UserRoleAssignmentApi);
  readonly rolesStore = inject(RolesStore);

  readonly selectedUserRoles = signal<UserRoleAssignment | null>(null);

  readonly loading = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly submitError = signal<AppHttpError | null>(null);
  readonly hasSubmitError = computed(() => this.submitError() !== null);

  readonly selectedRoleIds = signal<string[]>([]);
  readonly initialRoleIds = signal<string[]>([]);

  readonly isLoading = computed(
    () => this.loading() || this.rolesStore.loading()
  );

  readonly availableRoles = computed(() => this.rolesStore.roles());

  readonly isEmpty = computed(
    () =>
      !this.isLoading() &&
      !this.hasError() &&
      this.availableRoles().length === 0
  );

  readonly selectedCount = computed(() => this.selectedRoleIds().length);

  readonly isDirty = computed(() => {
    const current = [...this.selectedRoleIds()].sort();
    const initial = [...this.initialRoleIds()].sort();

    if (current.length !== initial.length) {
      return true;
    }

    return current.some((roleId, index) => roleId !== initial[index]);
  });

  async loadForUser(userId: string): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    this.submitError.set(null);
    this.selectedUserRoles.set(null);
    this.selectedRoleIds.set([]);
    this.initialRoleIds.set([]);

    try {
      if (!this.rolesStore.hasLoaded()) {
        await this.rolesStore.loadRoles();
      }

      const dto = await firstValueFrom(
        this.userRoleAssignmentApi.getUserRoles(userId)
      );
      const userRoleAssignment = toUserRoleAssignment(dto);
      const assignedRoleIds = userRoleAssignment.roles.map(role => role.id);

      this.selectedUserRoles.set(userRoleAssignment);
      this.selectedRoleIds.set(assignedRoleIds);
      this.initialRoleIds.set(assignedRoleIds);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.selectedUserRoles.set(null);
      this.selectedRoleIds.set([]);
      this.initialRoleIds.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  toggleRole(roleId: string, checked: boolean): void {
    this.selectedRoleIds.update(currentIds => {
      if (checked) {
        return currentIds.includes(roleId)
          ? currentIds
          : [...currentIds, roleId];
      }

      return currentIds.filter(currentId => currentId !== roleId);
    });
  }

  async save(userId: string): Promise<boolean> {
    this.submitting.set(true);
    this.submitError.set(null);

    try {
      const formValue: UserRoleAssignmentFormValue = {
        roleIds: this.selectedRoleIds(),
      };

      const payload = toAssignUserRolesRequest(formValue);

      await firstValueFrom(
        this.userRoleAssignmentApi.assignUserRoles(userId, payload)
      );

      this.initialRoleIds.set([...this.selectedRoleIds()]);
      await this.loadForUser(userId);

      return true;
    } catch (error) {
      this.submitError.set(error as AppHttpError);
      return false;
    } finally {
      this.submitting.set(false);
    }
  }

  isRoleSelected(roleId: string): boolean {
    return this.selectedRoleIds().includes(roleId);
  }

  clearError(): void {
    this.error.set(null);
  }

  clearSubmitError(): void {
    this.submitError.set(null);
  }

  resetState(): void {
    this.selectedUserRoles.set(null);
    this.selectedRoleIds.set([]);
    this.initialRoleIds.set([]);
    this.error.set(null);
    this.submitError.set(null);
    this.loading.set(false);
    this.submitting.set(false);
  }
}
