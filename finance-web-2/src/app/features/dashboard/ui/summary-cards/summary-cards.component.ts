import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { TenantSummaryResponse } from '../../../../entities/dashboard';

@Component({
  selector: 'app-summary-cards',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './summary-cards.component.html'
})
export class SummaryCardsComponent {
  // Solo recibe datos (Dumb Component)
  @Input() data: TenantSummaryResponse | null = null;

  get usagePercent(): number {
    if (!this.data || this.data.summary.users.total <= 0) {
      return 0;
    }

    return Math.min(100, Math.round((this.data.summary.users.active / this.data.summary.users.total) * 100));
  }
}
