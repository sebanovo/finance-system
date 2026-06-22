import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  LimitsService,
  LimitRuleResponse,
  CreateLimitRuleRequest,
  UpdateLimitRuleRequest,
  PageResponse
} from '../../../entities/limits';

export interface LimitRulesListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  page: PageResponse<LimitRuleResponse> | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class LimitRulesListUseCase {
  private readonly limitsService = inject(LimitsService);

  private readonly state = signal<LimitRulesListState>({
    status: 'idle',
    page: null,
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly page = computed(() => this.state().page);
  readonly data = computed(() => this.state().page?.content ?? []);
  readonly error = computed(() => this.state().error);

  async loadRules(page = 0, size = 20): Promise<void> {
    this.state.set({ status: 'loading', page: this.state().page, error: null });

    try {
      const response = await firstValueFrom(this.limitsService.listRules(page, size));

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
          error: response.message || 'No se pudieron cargar las reglas de límite' 
        });
      }
    } catch (err: any) {
      const errorMsg = err.error?.message || err.message || 'Error al conectar con el servidor';
      this.state.set({ status: 'error', page: null, error: errorMsg });
    }
  }

  async createRule(request: CreateLimitRuleRequest): Promise<void> {
    try {
      await firstValueFrom(this.limitsService.createRule(request));
      await this.loadRules();
    } catch (err: any) {
      throw err;
    }
  }

  async updateRule(id: string, request: UpdateLimitRuleRequest): Promise<void> {
    try {
      await firstValueFrom(this.limitsService.updateRule(id, request));
      await this.loadRules();
    } catch (err: any) {
      throw err;
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
