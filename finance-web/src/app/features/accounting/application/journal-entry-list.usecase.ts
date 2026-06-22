import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AccountingService, JournalEntryResponse, PageResponse } from '../../../entities/accounting';

export interface JournalEntryListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<JournalEntryResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class JournalEntryListUseCase {
  private readonly accountingService = inject(AccountingService);

  private readonly state = signal<JournalEntryListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadEntries(page = 0, size = 20): Promise<void> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.accountingService.listJournalEntries(page, size));

      if (response.success && response.data) {
        this.state.set({ 
          status: 'success', 
          page: response.data, 
          error: null 
        });
      } else {
        this.state.set({ 
          status: 'error', 
          page: null, 
          error: response.message || 'No se pudieron cargar los asientos contables' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }
}
