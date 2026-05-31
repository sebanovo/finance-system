// features/platform/application/platform-subscription-list.usecase.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { PlatformService, PlatformSubscription } from '../../../entities/platform/api/platform.service';

export interface PlatformSubscriptionListState {
  status: 'idle' | 'loading' | 'success' | 'error';
  subscriptions: PlatformSubscription[];
  error: string | null;
}

@Injectable({ providedIn: 'root' })
export class PlatformSubscriptionListUseCase {
  private platformService = inject(PlatformService);

  private state = signal<PlatformSubscriptionListState>({
    status: 'idle',
    subscriptions: [],
    error: null
  });

  readonly status = computed(() => this.state().status);
  readonly subscriptions = computed(() => this.state().subscriptions);
  readonly error = computed(() => this.state().error);

  async loadSubscriptions(): Promise<void> {
    this.state.set({ status: 'loading', subscriptions: [], error: null });

    try {
      const response = await firstValueFrom(this.platformService.getSubscriptions());
      if (response.success) {
        this.state.set({ status: 'success', subscriptions: response.data, error: null });
      } else {
        this.state.set({ status: 'error', subscriptions: [], error: response.message });
      }
    } catch (err: any) {
      this.state.set({ status: 'error', subscriptions: [], error: err.message || 'Error al cargar suscripciones' });
    }
  }

  resetState(): void {
    this.state.set({ status: 'idle', subscriptions: [], error: null });
  }
}