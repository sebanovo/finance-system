import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, TenantRoleResponse } from '../../../entities/access';

export interface RoleListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: TenantRoleResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class RoleListUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<RoleListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRoles(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accessService.getRoles());

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
          error: response.message || 'No se pudieron cargar los roles' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async reloadRoles(): Promise<void> {
    const currentData = this.state().data;
    this.state.set({ status: 'loading', data: currentData, error: null });

    try {
      const response = await firstValueFrom(this.accessService.getRoles());

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          data: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          data: currentData,
          error: response.message || 'No se pudieron recargar los roles' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: currentData, error: errorMsg });
    }
  }

  // Métodos extra para activar/desactivar (toggle) en la lista si lo necesitamos
  async toggleRoleStatus(id: string, currentActiveStatus: boolean): Promise<boolean> {
    try {
      if (currentActiveStatus) {
        await firstValueFrom(this.accessService.deactivateRole(id));
      } else {
        await firstValueFrom(this.accessService.activateRole(id));
      }
      return true; // éxito
    } catch (error) {
      console.error('Error toggling role status', error);
      return false; // fallo
    }
  }
}
