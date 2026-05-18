import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { JournalEntryListUseCase } from '../../features/accounting';

@Component({
  selector: 'app-accounting-journal-entries-page',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe, CurrencyPipe],
  template: `
    <div class="space-y-6 relative">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold tracking-tight text-foreground">Asientos de Diario</h2>
          <p class="text-muted-foreground">Visualiza todos los movimientos contables registrados.</p>
        </div>
      </div>

      <!-- Estado: Error Lista -->
      @if (useCase.status() === 'error') {
        <div class="p-6 bg-destructive/10 border border-destructive/20 rounded-xl flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-destructive mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-destructive">Error al cargar asientos</h3>
            <p class="text-sm text-destructive/80 mt-1">{{ useCase.error() }}</p>
            <button (click)="loadEntries()" class="mt-3 text-sm font-medium text-destructive hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      <!-- Estado: Cargando Inicial -->
      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center p-12 text-muted-foreground gap-4">
          <svg class="animate-spin h-8 w-8 text-primary" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando asientos...</p>
        </div>
      }

      <!-- Estado: Éxito o Recargando -->
      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="bg-card border border-border rounded-xl shadow-sm overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="text-xs text-muted-foreground uppercase bg-muted/50 border-b border-border">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Nº Asiento</th>
                  <th scope="col" class="px-6 py-4 font-medium">Referencia</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Total Débitos</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Total Créditos</th>
                  <th scope="col" class="px-6 py-4 font-medium text-center">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Fecha de Registro</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-border">
                @for (entry of useCase.data(); track entry.id) {
                  <tr class="hover:bg-muted/30 transition-colors">
                    <td class="px-6 py-4 font-medium text-foreground">{{ entry.entryNumber }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ entry.reference || '-' }}</td>
                    <td class="px-6 py-4 text-muted-foreground">{{ entry.entryType }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ entry.totalDebits | currency:'USD' }}</td>
                    <td class="px-6 py-4 font-medium text-right text-foreground">{{ entry.totalCredits | currency:'USD' }}</td>
                    <td class="px-6 py-4 text-center">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="{
                              'bg-green-500/10 text-green-600': entry.status === 'POSTED',
                              'bg-yellow-500/10 text-yellow-600': entry.status === 'DRAFT',
                              'bg-red-500/10 text-red-600': entry.status === 'VOIDED'
                            }">
                        {{ entry.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-muted-foreground text-sm">{{ entry.createdAt | date:'short' }}</td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="7" class="px-6 py-12 text-center text-muted-foreground">
                      No hay asientos contables registrados.
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `
})
export class AccountingJournalEntriesPageComponent implements OnInit {
  public readonly useCase = inject(JournalEntryListUseCase);

  ngOnInit() {
    this.loadEntries();
  }

  loadEntries() {
    this.useCase.loadEntries();
  }
}
