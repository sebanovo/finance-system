// features/platform/application/platform-plan-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformPlan } from '../../../entities/platform/api/platform.service';

export interface PlatformPlanListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  plans: PlatformPlan[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformPlanListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformPlanListState>({
    status: 'idle',
    plans: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly plans = computed(() => this.state().plans);
  readonly error = computed(() => this.state().error);

  async loadPlans(): Promise<void> {
    this.state.set({ status: 'loading', plans: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getPlans());
      if (response.success) {
        this.state.set({ status: 'success', plans: response.data, error: null });
      } else {
        this.state.set({ status: 'error', plans: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', plans: [], error: err.message || 'Error al cargar planes' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', plans: [], error: null });
  }
}