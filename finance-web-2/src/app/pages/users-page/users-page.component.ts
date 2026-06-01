import { Component, inject, OnInit, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListUseCase, UserTableComponent, UserCreateUseCase, UserCreateFormComponent, UserUpdateUseCase, UserStatusUseCase, UserEditFormComponent } from '../../features/user-management';
import { CreateTenantUserRequest, TenantUserResponse, UpdateTenantUserRequest } from '../../entities/user';
import { UserRoleAssignmentComponent, UserRoleUseCase } from '../../features/user-role-management';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, UserTableComponent, UserCreateFormComponent, UserEditFormComponent, UserRoleAssignmentComponent],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Usuarios</h2>
          <p class="text-muted-foreground">Gestiona los accesos y permisos de tu equipo.</p>
        </div>
        
        <button 
          (click)="openCreateModal()"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nuevo Usuario
        </button>
      </div>

      <!-- Estado: Cargando -->
      @if (userListUseCase.status() === 'loading' && userListUseCase.data().length === 0) {
        <div class="flex items-center justify-center p-12">
          <div class="flex flex-col items-center gap-4 text-muted-foreground">
            <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p>Cargando lista de usuarios...</p>
          </div>
        </div>
      }

      <!-- Estado: Error -->
      @if (userListUseCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar usuarios</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ userListUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Éxito (Tabla) -->
      @if (userListUseCase.status() === 'success' || (userListUseCase.status() === 'loading' && userListUseCase.data().length > 0)) {
        <div class="relative">
          @if (userListUseCase.status() === 'loading') {
            <div class="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-primary text-primary-foreground px-3 py-1 rounded-full text-xs shadow-md animate-pulse z-10">Actualizando...</div>
          }
          <app-user-table 
            [users]="userListUseCase.data()"
            (manageRoles)="openRoleModal($event)"
            (editUser)="openEditModal($event)"
            (toggleStatus)="handleToggleStatus($event)">
          </app-user-table>
        </div>
      }
    </div>

    <!-- Modal de Creación -->
    @if (isCreateModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeCreateModal()"></div>
        
        <!-- Modal Panel -->
        <div class="relative bg-card w-full max-w-lg mx-4 rounded-xl shadow-lg border border-border flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h3 class="text-lg font-semibold text-foreground">Crear Nuevo Usuario</h3>
            <button (click)="closeCreateModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1" [disabled]="userCreateUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>
          <!-- Body -->
          <div class="p-6 overflow-y-auto">
            @if (userCreateUseCase.error()) {
              <div class="mb-4 p-3 bg-destructive/10 text-destructive border border-destructive/20 rounded-md text-sm">
                {{ userCreateUseCase.error() }}
              </div>
            }
            <app-user-create-form
              [status]="userCreateUseCase.status()"
              (formSubmit)="handleCreateUser($event)"
              (cancel)="closeCreateModal()"
            />
          </div>
        </div>
      </div>
    }

    <!-- Modal de Edición -->
    @if (isEditModalOpen() && selectedUserToEdit()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeEditModal()"></div>
        
        <!-- Modal Panel -->
        <div class="relative bg-card w-full max-w-lg mx-4 rounded-xl shadow-lg border border-border flex flex-col max-h-[90vh] animate-in zoom-in-95 duration-200">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h3 class="text-lg font-semibold text-foreground">Editar Usuario</h3>
            <button (click)="closeEditModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1" [disabled]="userUpdateUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>
          <!-- Body -->
          <div class="p-6 overflow-y-auto">
            @if (userUpdateUseCase.error()) {
              <div class="mb-4 p-3 bg-destructive/10 text-destructive border border-destructive/20 rounded-md text-sm">
                {{ userUpdateUseCase.error() }}
              </div>
            }
            <app-user-edit-form
              [status]="userUpdateUseCase.status()"
              [user]="selectedUserToEdit()!"
              (formSubmit)="handleUpdateUser($event)"
              (cancel)="closeEditModal()"
            />
          </div>
        </div>
      </div>
    }

    <!-- Modal Asignación de Roles -->
    @if (isRoleModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-end">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeRoleModal()"></div>
        
        <!-- Slide-over Panel -->
        <div class="relative bg-card w-full max-w-md h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
          
          <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
            <h3 class="text-lg font-semibold text-foreground">Asignar Roles</h3>
            <button (click)="closeRoleModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="userRoleUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>

          <div class="flex-1 overflow-hidden">
            @if (userRoleUseCase.error()) {
              <div class="m-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-lg text-sm">
                {{ userRoleUseCase.error() }}
              </div>
            }

            <app-user-role-assignment
              [status]="userRoleUseCase.status()"
              [user]="selectedUser()"
              [availableRoles]="userRoleUseCase.availableRoles()"
              [assignedRoles]="userRoleUseCase.userRoles()"
              (submitRoles)="handleAssignRoles($event)"
              (cancel)="closeRoleModal()">
            </app-user-role-assignment>
          </div>
        </div>
      </div>
    }
  `
})
export class UsersPageComponent implements OnInit {
  public readonly userListUseCase = inject(UserListUseCase);
  public readonly userCreateUseCase = inject(UserCreateUseCase);
  public readonly userUpdateUseCase = inject(UserUpdateUseCase);
  public readonly userStatusUseCase = inject(UserStatusUseCase);
  public readonly userRoleUseCase = inject(UserRoleUseCase);

  public readonly isCreateModalOpen = signal(false);
  public readonly isEditModalOpen = signal(false);
  public readonly isRoleModalOpen = signal(false);
  public readonly selectedUser = signal<TenantUserResponse | null>(null);
  public readonly selectedUserToEdit = signal<TenantUserResponse | null>(null);
  
  private readonly toastService = inject(ToastService);

  constructor() {
    // Éxito al crear usuario
    effect(() => {
      if (this.userCreateUseCase.status() === 'success') {
        this.closeCreateModal();
        this.userCreateUseCase.resetState();
        this.userListUseCase.reloadUsers();
        this.toastService.success('Usuario creado correctamente');
      }
    }, { allowSignalWrites: true });

    // Éxito al editar usuario
    effect(() => {
      if (this.userUpdateUseCase.status() === 'success') {
        this.closeEditModal();
        this.userUpdateUseCase.resetState();
        this.userListUseCase.reloadUsers();
        this.toastService.success('Usuario actualizado correctamente');
      }
    }, { allowSignalWrites: true });

    // Éxito al asignar roles
    effect(() => {
      if (this.userRoleUseCase.status() === 'success') {
        this.closeRoleModal();
        this.userRoleUseCase.resetState();
        this.toastService.success('Roles actualizados correctamente');
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit(): void {
    this.userListUseCase.loadUsers();
  }

  retry(): void {
    this.userListUseCase.loadUsers();
  }

  openCreateModal(): void {
    this.userCreateUseCase.resetState();
    this.isCreateModalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.userCreateUseCase.status() === 'loading') return;
    this.isCreateModalOpen.set(false);
  }

  handleCreateUser(request: CreateTenantUserRequest): void {
    this.userCreateUseCase.createUser(request);
  }

  // --- Editar Usuario ---
  openEditModal(user: TenantUserResponse): void {
    this.userUpdateUseCase.resetState();
    this.selectedUserToEdit.set(user);
    this.isEditModalOpen.set(true);
  }

  closeEditModal(): void {
    if (this.userUpdateUseCase.status() === 'loading') return;
    this.isEditModalOpen.set(false);
    setTimeout(() => this.selectedUserToEdit.set(null), 300);
  }

  handleUpdateUser(request: UpdateTenantUserRequest): void {
    const user = this.selectedUserToEdit();
    if (user) {
      this.userUpdateUseCase.updateUser(user.id.toString(), request);
    }
  }

  // --- Estado de Usuario (Activar/Desactivar) ---
  async handleToggleStatus(user: TenantUserResponse): Promise<void> {
    const action = user.active ? 'desactivar' : 'activar';
    if (confirm(`¿Estás seguro que deseas ${action} a este usuario?`)) {
      const success = await this.userStatusUseCase.toggleStatus(user.id.toString(), user.active);
      if (success) {
        this.toastService.success(`Usuario ${user.active ? 'desactivado' : 'activado'} correctamente`);
        this.userListUseCase.reloadUsers();
      } else {
        this.toastService.error(this.userStatusUseCase.error() || 'Error al cambiar el estado del usuario');
      }
    }
  }

  // --- Roles Modal ---
  openRoleModal(user: TenantUserResponse): void {
    this.userRoleUseCase.resetState();
    this.selectedUser.set(user);
    // Cargamos los roles de este usuario
    this.userRoleUseCase.loadRolesForUser(user.id.toString());
    this.isRoleModalOpen.set(true);
  }

  closeRoleModal(): void {
    if (this.userRoleUseCase.status() === 'loading') return;
    this.isRoleModalOpen.set(false);
    setTimeout(() => this.selectedUser.set(null), 300); // limpiar despues de animación
  }

  handleAssignRoles(roleIds: string[]): void {
    const user = this.selectedUser();
    if (user) {
      this.userRoleUseCase.assignRoles(user.id.toString(), roleIds);
    }
  }
}
