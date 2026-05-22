import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccessService, SystemPermissionResponse } from '../../../entities/access';

export interface PermissionListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: SystemPermissionResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class PermissionListUseCase {
  private readonly accessService = inject(AccessService);

  private readonly state = signal<PermissionListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadPermissions(): Promise<void> {
    // Si ya los tenemos cargados, no volvemos a llamar (cache simple), los permisos rara vez cambian.
    if (this.state().data.length > 0 && this.state().status === 'success') {
      return;
    }

    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accessService.getPermissions());

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
          error: response.message || 'No se pudieron cargar los permisos del sistema' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }
}
