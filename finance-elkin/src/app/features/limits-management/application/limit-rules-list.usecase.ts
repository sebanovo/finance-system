import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { LimitsService, LimitRuleResponse } from '../../../entities/limits';

export interface LimitRulesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  data: LimitRuleResponse[];
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class LimitRulesListUseCase {
  private readonly limitsService = inject(LimitsService);

  private readonly state = signal<LimitRulesListState>({
    status: 'idle',
    data: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly data = computed(() => this.state().data);
  readonly error = computed(() => this.state().error);

  async loadRules(): Promise<void> {
    this.state.set({ status: 'loading', data: [], error: null });

    try {
      const response = await firstValueFrom(this.limitsService.listRules());

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
          error: response.message || 'No se pudieron cargar las reglas de límite' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', data: [], error: errorMsg });
    }
  }

  async deleteRule(id: string): Promise<boolean> {
    try {
      await firstValueFrom(this.limitsService.deleteRule(id));
      await this.loadRules();
      return true;
    } catch (err) {
      return false;
    }
  }
}
