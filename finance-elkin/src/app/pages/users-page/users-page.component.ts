import { Component, inject, OnInit, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListUseCase, UserTableComponent, UserCreateUseCase, UserCreateFormComponent } from '../../features/user-management';
import { CreateTenantUserRequest } from '../../entities/user';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, UserTableComponent, UserCreateFormComponent],
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
      <div *ngIf="userListUseCase.status() === 'loading' && userListUseCase.data().length === 0" class="flex items-center justify-center p-12">
        <div class="flex flex-col items-center gap-4 text-muted-foreground">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando lista de usuarios...</p>
        </div>
      </div>

      <!-- Estado: Error -->
      <div *ngIf="userListUseCase.status() === 'error'" class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <div>
          <h3 class="font-semibold text-destructive">Error al cargar usuarios</h3>
          <p class="text-sm text-destructive/80 mt-1">{{ userListUseCase.error() }}</p>
          <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
        </div>
      </div>

      <!-- Estado: Éxito (Tabla) -->
      <div *ngIf="userListUseCase.status() === 'success' || (userListUseCase.status() === 'loading' && userListUseCase.data().length > 0)">
        <div *ngIf="userListUseCase.status() === 'loading'" class="absolute top-0 left-1/2 transform -translate-x-1/2 bg-primary text-primary-foreground px-3 py-1 rounded-b-md text-xs shadow-md animate-pulse">Actualizando...</div>
        <app-user-table [users]="userListUseCase.data()"></app-user-table>
      </div>
      
      <!-- Mensaje de Éxito de Creación (Toast simple) -->
      <div *ngIf="showSuccessToast()" class="fixed bottom-4 right-4 bg-primary text-primary-foreground px-4 py-3 rounded-md shadow-lg flex items-center gap-2 animate-in fade-in slide-in-from-bottom-4">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
        Usuario creado correctamente
      </div>

    </div>

    <!-- Modal de Creación -->
    <div *ngIf="isCreateModalOpen()" class="fixed inset-0 z-50 flex items-center justify-center">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-background/80 backdrop-blur-sm" (click)="closeCreateModal()"></div>
      
      <!-- Modal Panel -->
      <div class="relative bg-card w-full max-w-lg mx-4 rounded-xl shadow-lg border border-border flex flex-col max-h-[90vh]">
        
        <!-- Modal Header -->
        <div class="flex items-center justify-between px-6 py-4 border-b border-border">
          <h3 class="text-lg font-semibold text-foreground">Crear Nuevo Usuario</h3>
          <button (click)="closeCreateModal()" class="text-muted-foreground hover:text-foreground transition-colors p-1" [disabled]="userCreateUseCase.status() === 'loading'">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" x2="6" y1="6" y2="18"/><line x1="6" x2="18" y1="6" y2="18"/></svg>
          </button>
        </div>

        <!-- Modal Body -->
        <div class="p-6 overflow-y-auto">
          <div *ngIf="userCreateUseCase.error()" class="mb-4 p-3 bg-destructive/10 text-destructive border border-destructive/20 rounded-md text-sm">
            {{ userCreateUseCase.error() }}
          </div>
          
          <app-user-create-form
            [status]="userCreateUseCase.status()"
            (formSubmit)="handleCreateUser($event)"
            (cancel)="closeCreateModal()"
          />
        </div>
      </div>
    </div>
  `
})
export class UsersPageComponent implements OnInit {
  public readonly userListUseCase = inject(UserListUseCase);
  public readonly userCreateUseCase = inject(UserCreateUseCase);

  public readonly isCreateModalOpen = signal(false);
  public readonly showSuccessToast = signal(false);

  constructor() {
    // Reaccionar al estado de éxito en la creación de usuario
    effect(() => {
      if (this.userCreateUseCase.status() === 'success') {
        this.closeCreateModal();
        this.userCreateUseCase.resetState();
        
        // Recargar la lista silenciosamente
        this.userListUseCase.reloadUsers();
        
        // Mostrar mensaje temporal
        this.showSuccessToast.set(true);
        setTimeout(() => this.showSuccessToast.set(false), 3000);
      }
    }, { allowSignalWrites: true }); // Permitimos escribir señales dentro del effect para manejar modales
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
    // Prevenir cierre si está cargando
    if (this.userCreateUseCase.status() === 'loading') return;
    this.isCreateModalOpen.set(false);
  }

  handleCreateUser(request: CreateTenantUserRequest): void {
    this.userCreateUseCase.createUser(request);
  }
}
