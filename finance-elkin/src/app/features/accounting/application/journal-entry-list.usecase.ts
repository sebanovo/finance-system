import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountingService, JournalEntryResponse } from '../../../entities/accounting';

export interface JournalEntryListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: JournalEntryResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class JournalEntryListUseCase {
  private readonly accountingService = inject(AccountingService);

  private readonly state = signal<JournalEntryListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadEntries(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.accountingService.listJournalEntries());

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
          error: response.message || 'No se pudieron cargar los asientos contables' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }
}
