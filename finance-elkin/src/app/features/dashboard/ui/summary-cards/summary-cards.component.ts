import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantSummaryResponse } from '../../../../entities/dashboard';

@Component({
  selector: 'app-summary-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './summary-cards.component.html'
})
export class SummaryCardsComponent {
  // Solo recibe datos (Dumb Component)
  @Input() data: TenantSummaryResponse | null = null;
}
