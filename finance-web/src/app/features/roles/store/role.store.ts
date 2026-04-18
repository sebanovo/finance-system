import { computed, inject, Injectable, signal } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { AppHttpError } from "../../../core/http/models/app-http-error.model";
import {
  toCreateRoleRequest,
  toRole,
  toRoles,
  toUpdateRoleRequest,
} from "../adapters/role.adapter";
import {
  Role,
  RoleUpsertFormValue,
  UpdateRoleRequest,
} from "../model/role.type";
import { RolesApi } from "../services/role.service";

@Injectable({
  providedIn: "root",
})
export class RolesStore {
  private readonly rolesApi = inject(RolesApi);

  readonly roles = signal<Role[]>([]);
  readonly loading = signal(false);
  readonly hasLoaded = signal(false);
  readonly error = signal<AppHttpError | null>(null);
  readonly hasError = computed(() => this.error() !== null);

  readonly submitting = signal(false);
  readonly createError = signal<AppHttpError | null>(null);
  readonly hasCreateError = computed(() => this.createError() !== null);

  readonly selectedRole = signal<Role | null>(null);
  readonly selectedRoleLoading = signal(false);
  readonly selectedRoleError = signal<AppHttpError | null>(null);
  readonly hasSelectedRoleError = computed(
    () => this.selectedRoleError() !== null
  );

  readonly updateError = signal<AppHttpError | null>(null);
  readonly hasUpdateError = computed(() => this.updateError() !== null);

  readonly togglingRoleIds = signal<string[]>([]);
  readonly toggleError = signal<AppHttpError | null>(null);
  readonly hasToggleError = computed(() => this.toggleError() !== null);

  readonly isEmpty = computed(
    () => this.hasLoaded() && !this.loading() && this.roles().length === 0
  );

  async loadRoles(): Promise<void> {
    this.loading.set(true);
    this.error.set(null);

    try {
      const roleDtos = await firstValueFrom(this.rolesApi.getRoles());
      this.roles.set(toRoles(roleDtos));
      this.hasLoaded.set(true);
    } catch (error) {
      this.error.set(error as AppHttpError);
      this.roles.set([]);
      this.hasLoaded.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  async createRole(formValue: RoleUpsertFormValue): Promise<Role | null> {
    this.submitting.set(true);
    this.createError.set(null);

    try {
      const payload = toCreateRoleRequest(formValue);
      const createdRoleDto = await firstValueFrom(
        this.rolesApi.createRole(payload)
      );
      const createdRole = toRole(createdRoleDto);

      this.roles.update(currentRoles => [createdRole, ...currentRoles]);

      return createdRole;
    } catch (error) {
      this.createError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async loadRoleById(roleId: string): Promise<Role | null> {
    this.selectedRoleLoading.set(true);
    this.selectedRoleError.set(null);
    this.selectedRole.set(null);

    try {
      const roleDto = await firstValueFrom(this.rolesApi.getRoleById(roleId));
      const role = toRole(roleDto);

      this.selectedRole.set(role);

      return role;
    } catch (error) {
      this.selectedRoleError.set(error as AppHttpError);
      this.selectedRole.set(null);
      return null;
    } finally {
      this.selectedRoleLoading.set(false);
    }
  }

  async updateRole(
    roleId: string,
    formValue: RoleUpsertFormValue
  ): Promise<Role | null> {
    this.submitting.set(true);
    this.updateError.set(null);

    try {
      const payload: UpdateRoleRequest = toUpdateRoleRequest(formValue);
      const updatedRoleDto = await firstValueFrom(
        this.rolesApi.updateRole(roleId, payload)
      );
      const updatedRole = toRole(updatedRoleDto);

      this.selectedRole.set(updatedRole);

      this.roles.update(currentRoles =>
        currentRoles.map(role =>
          role.id === updatedRole.id ? updatedRole : role
        )
      );

      return updatedRole;
    } catch (error) {
      this.updateError.set(error as AppHttpError);
      return null;
    } finally {
      this.submitting.set(false);
    }
  }

  async toggleRoleActiveState(role: Role): Promise<Role | null> {
    this.startTogglingRole(role.id);
    this.toggleError.set(null);

    try {
      const updatedRoleDto = role.isActive
        ? await firstValueFrom(this.rolesApi.deactivateRole(role.id))
        : await firstValueFrom(this.rolesApi.activateRole(role.id));

      const updatedRole = toRole(updatedRoleDto);

      this.roles.update(currentRoles =>
        currentRoles.map(currentRole =>
          currentRole.id === updatedRole.id ? updatedRole : currentRole
        )
      );

      if (this.selectedRole()?.id === updatedRole.id) {
        this.selectedRole.set(updatedRole);
      }

      return updatedRole;
    } catch (error) {
      this.toggleError.set(error as AppHttpError);
      return null;
    } finally {
      this.finishTogglingRole(role.id);
    }
  }

  isRoleToggling(roleId: string): boolean {
    return this.togglingRoleIds().includes(roleId);
  }

  private startTogglingRole(roleId: string): void {
    this.togglingRoleIds.update(currentIds =>
      currentIds.includes(roleId) ? currentIds : [...currentIds, roleId]
    );
  }

  private finishTogglingRole(roleId: string): void {
    this.togglingRoleIds.update(currentIds =>
      currentIds.filter(currentId => currentId !== roleId)
    );
  }

  clearError(): void {
    this.error.set(null);
  }

  clearCreateError(): void {
    this.createError.set(null);
  }

  reloadRoles(): Promise<void> {
    return this.loadRoles();
  }

  clearSelectedRoleError(): void {
    this.selectedRoleError.set(null);
  }

  clearUpdateError(): void {
    this.updateError.set(null);
  }

  clearSelectedRole(): void {
    this.selectedRole.set(null);
  }

  clearToggleError(): void {
    this.toggleError.set(null);
  }
}
