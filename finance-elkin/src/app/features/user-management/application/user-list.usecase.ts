import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UserService, TenantUserResponse } from '../../../entities/user';

export interface UserListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantUserResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserListUseCase {
  private readonly userService = inject(UserService);

  private readonly state = signal<UserListState>({
    status: 'idle',
    data: [],
    error: null
  });

  // Selectores
  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadUsers(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.userService.getUsers());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: [], 
          error: response.message || 'No se pudieron cargar los usuarios' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async reloadUsers(): Promise<void> {
    // Al recargar, mantenemos la data actual pero cambiamos el status para mostrar algún indicador si se desea
    // o simplemente actualizamos silenciosamente
    const currentData = this.state().data;
    this.state.set({ status: 'loading', data: currentData, error: null });

    try {
      const response = await firstValueFrom(this.userService.getUsers());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: currentData, // Mantenemos la data vieja en caso de error? Depende UX, pero el prompt dice "evitar parpadeo visual".
          error: response.message || 'No se pudieron recargar los usuarios' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: currentData, error: errorMsg });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', data: [], error: null });
  }
}
