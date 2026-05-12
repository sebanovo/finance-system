import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SummaryCardsComponent, SummaryUseCase } from '../../features/dashboard';

@Component({
  selector: 'app-summary-page',
  standalone: true,
  imports: [CommonModule, SummaryCardsComponent],
  template: `
    <div class="space-y-6">
      <div>
        <h2 class="text-2xl font-bold tracking-tight text-foreground">Resumen de la Organización</h2>
        <p class="text-muted-foreground">Métricas clave y estado actual de tu suscripción.</p>
      </div>

      <!-- Estado: Cargando -->
      <div *ngIf="summaryUseCase.status() === 'loading'" class="flex items-center justify-center p-12">
        <div class="flex flex-col items-center gap-4 text-muted-foreground">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando información del panel...</p>
        </div>
      </div>

      <!-- Estado: Error -->
      <div *ngIf="summaryUseCase.status() === 'error'" class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
        <div>
          <h3 class="font-semibold text-destructive">Error al cargar datos</h3>
          <p class="text-sm text-destructive/80 mt-1">{{ summaryUseCase.error() }}</p>
          <button (click)="retry()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
        </div>
      </div>

      <!-- Estado: Éxito (Presentational Component) -->
      <div *ngIf="summaryUseCase.status() === 'success'">
        <app-summary-cards [data]="summaryUseCase.data()"></app-summary-cards>
      </div>
    </div>
  `
})
export class SummaryPageComponent implements OnInit {
  public readonly summaryUseCase = inject(SummaryUseCase);

  ngOnInit(): void {
    // Al inicializar la página, cargamos los datos
    this.summaryUseCase.loadSummary();
  }

  retry(): void {
    this.summaryUseCase.loadSummary();
  }
}
