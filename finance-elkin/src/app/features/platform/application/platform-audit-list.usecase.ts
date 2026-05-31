// features/platform/application/platform-audit-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, AuditEvent } from '../../../entities/platform/api/platform.service';

export interface PlatformAuditListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  events: AuditEvent[];  // ✅ Asegurar que sea AuditEvent[]
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformAuditListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformAuditListState>({
    status: 'idle',
    events: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly events = computed(() => this.state().events);  // ✅ Esto devuelve AuditEvent[]
  readonly error = computed(() => this.state().error);

  async loadAuditEvents(limit: number = 50): Promise<void> {
    this.state.set({ status: 'loading', events: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getAuditEvents(limit));
      if (response.success && response.data) {
        this.state.set({ status: 'success', events: response.data, error: null });
      } else {
        this.state.set({ status: 'error', events: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', events: [], error: err.message || 'Error al cargar auditoría' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', events: [], error: null });
  }
}