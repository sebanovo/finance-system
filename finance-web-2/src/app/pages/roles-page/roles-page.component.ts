import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionListUseCase, RoleFormComponent, RoleFormUseCase, RoleListUseCase, RoleTableComponent } from '../../features/role-management';
import { CreateTenantRoleRequest, TenantRoleResponse, UpdateTenantRoleRequest } from '../../entities/access';

@Component({
  selector: 'app-roles-page',
  standalone: true,
  imports: [CommonModule, RoleTableComponent, RoleFormComponent],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Roles y Permisos</h2>
          <p class="text-muted-foreground">Define los niveles de acceso para tu equipo.</p>
        </div>
        
        <button 
          type="button"
          (click)="openRoleModal()"
          class="bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" x2="12" y1="5" y2="19"/><line x1="5" x2="19" y1="12" y2="12"/></svg>
          Nuevo Rol
        </button>
      </div>

      <!-- Estado: Error Lista -->
      @if (roleListUseCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar roles</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ roleListUseCase.error() }}</p>
            <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (roleListUseCase.status() === 'loading' && roleListUseCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando roles...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (roleListUseCase.status() === 'success' || (roleListUseCase.status() === 'loading' && roleListUseCase.data().length > 0)) {
        @if (roleListUseCase.status() === 'loading') {
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 bg-primary text-primary-foreground px-3 py-1 rounded-b-md text-xs shadow-md animate-pulse z-10">Actualizando...</div>
        }
        
        <app-role-table 
          [roles]="roleListUseCase.data()"
          (edit)="openRoleModal($event)"
          (toggleStatus)="handleToggleStatus($event.id, $event.currentStatus)">
        </app-role-table>
      }

      <!-- Toast Éxito -->
      @if (showSuccessToast()) {
        <div class="fixed bottom-4 right-4 bg-primary text-primary-foreground px-4 py-3 rounded-md shadow-lg flex items-center gap-2 animate-in fade-in slide-in-from-bottom-4 z-50">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          Operación exitosa
        </div>
      }
    </div>

    <!-- Modal Formulario de Rol -->
    @if (isModalOpen()) {
      <div class="fixed inset-0 z-50 flex items-center justify-end">
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-background/80 backdrop-blur-sm animate-in fade-in" (click)="closeRoleModal()"></div>
        
        <!-- Slide-over panel -->
        <div class="relative bg-card w-full max-w-2xl h-full shadow-2xl border-l border-border flex flex-col animate-in slide-in-from-right duration-300">
          
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
            <h3 class="text-lg font-semibold text-foreground">
              {{ roleToEdit() ? 'Editar Rol' : 'Crear Nuevo Rol' }}
            </h3>
            <button (click)="closeRoleModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1 rounded-md hover:bg-muted" [disabled]="roleFormUseCase.status() === 'loading'">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
            </button>
          </div>

          <!-- Body -->
          <div class="flex-1 overflow-hidden">
            @if (roleFormUseCase.error()) {
              <div class="m-6 p-4 bg-destructive/10 text-destructive border border-destructive/20 rounded-lg text-sm">
                {{ roleFormUseCase.error() }}
              </div>
            }

            @if (permissionListUseCase.status() === 'loading') {
              <div class="flex justify-center p-12 text-muted-foreground">Cargando permisos disponibles...</div>
            } @else if (permissionListUseCase.status() === 'error') {
              <div class="m-6 p-4 bg-destructive/10 text-destructive rounded-lg text-sm">
                No se pudieron cargar los permisos: {{ permissionListUseCase.error() }}
              </div>
            } @else {
              <app-role-form
                [status]="roleFormUseCase.status()"
                [roleToEdit]="roleToEdit()"
                [permissions]="permissionListUseCase.data()"
                (submitCreate)="handleCreateRole($event)"
                (submitUpdate)="handleUpdateRole($event.id, $event.request)"
                (cancel)="closeRoleModal()">
              </app-role-form>
            }
          </div>
        </div>
      </div>
    }
  `
})
export class RolesPageComponent implements OnInit {
  public readonly roleListUseCase = inject(RoleListUseCase);
  public readonly permissionListUseCase = inject(PermissionListUseCase);
  public readonly roleFormUseCase = inject(RoleFormUseCase);

  public readonly isModalOpen = signal(false);
  public readonly roleToEdit = signal<TenantRoleResponse | null>(null);
  public readonly showSuccessToast = signal(false);

  constructor() {
    effect(() => {
      if (this.roleFormUseCase.status() === 'success') {
        this.closeRoleModal();
        this.roleFormUseCase.resetState();
        this.roleListUseCase.reloadRoles();
        this.triggerSuccessToast();
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit(): void {
    this.roleListUseCase.loadRoles();
    this.permissionListUseCase.loadPermissions(); // Pre-cargar permisos
  }

  retry(): void {
    this.roleListUseCase.loadRoles();
  }

  openRoleModal(role: TenantRoleResponse | null = null): void {
    this.roleFormUseCase.resetState();
    this.roleToEdit.set(role);
    this.permissionListUseCase.loadPermissions(); // Asegurar que estén cargados
    this.isModalOpen.set(true);
  }

  closeRoleModal(): void {
    if (this.roleFormUseCase.status() === 'loading') return;
    this.isModalOpen.set(false);
    setTimeout(() => this.roleToEdit.set(null), 300); // Esperar animación
  }

  handleCreateRole(request: CreateTenantRoleRequest): void {
    this.roleFormUseCase.createRole(request);
  }

  handleUpdateRole(id: string, request: UpdateTenantRoleRequest): void {
    this.roleFormUseCase.updateRole(id, request);
  }

  async handleToggleStatus(id: string, currentStatus: boolean): Promise<void> {
    const success = await this.roleListUseCase.toggleRoleStatus(id, currentStatus);
    if (success) {
      this.roleListUseCase.reloadRoles();
      this.triggerSuccessToast();
    }
  }

  private triggerSuccessToast(): void {
    this.showSuccessToast.set(true);
    setTimeout(() => this.showSuccessToast.set(false), 3000);
  }
}
