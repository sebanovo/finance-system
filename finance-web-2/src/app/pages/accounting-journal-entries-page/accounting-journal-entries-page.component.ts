import { CommonModule, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { JournalEntryListUseCase } from '../../features/accounting';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';

@Component({
  selector: 'app-accounting-journal-entries-page',
  standalone: true,
  imports: [CommonModule, PlatformPaginationComponent, LucideAngularModule],
  providers: [DatePipe],
  template: `
    <div class="space-y-6 relative">
      <section class="rounded-[28px] border border-[#C8E6C9] bg-gradient-to-br from-white via-[#F7FBF3] to-[#EAF6EB] p-6 shadow-[0_18px_50px_rgba(27,94,32,0.08)]">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div class="space-y-3">
            <div class="inline-flex items-center gap-2 rounded-full border border-[#C8E6C9] bg-white/80 px-3 py-1 text-xs font-semibold uppercase tracking-[0.16em] text-[#2E7D32]">
              <span class="h-2 w-2 rounded-full bg-[#4CAF50]"></span>
              Contabilidad
            </div>
            <div>
              <h1 class="text-3xl font-black tracking-tight text-[#1B5E20] sm:text-4xl">
                Asientos contables
              </h1>
              <p class="mt-2 max-w-3xl text-sm leading-6 text-[#5F6F5F]">
                Revisa los asientos generados por el sistema y su balance operativo.
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="loadEntries()"
              class="inline-flex cursor-pointer items-center gap-2 rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9]">
              <lucide-icon name="refresh-ccw" [size]="16"></lucide-icon>
              Recargar
            </button>
          </div>
        </div>
      </section>

      <section class="grid gap-4 md:grid-cols-3">
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">En página</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ currentPageCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Asientos visibles actualmente</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Balanceados</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ balancedCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Asientos con débitos y créditos en equilibrio</p>
        </div>
        <div class="rounded-2xl border border-[#C8E6C9] bg-white p-5 shadow-sm">
          <p class="text-sm font-semibold text-[#567157]">Posteados</p>
          <p class="mt-4 text-3xl font-black text-[#1B5E20]">{{ postedCount() }}</p>
          <p class="mt-2 text-xs text-[#6B7D6C]">Asientos ya contabilizados</p>
        </div>
      </section>

      @if (useCase.status() === 'error') {
        <div class="rounded-2xl border border-red-200 bg-red-50 p-6 flex items-start gap-4">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-red-700 mt-0.5"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
          <div>
            <h3 class="font-semibold text-red-700">Error al cargar asientos</h3>
            <p class="mt-1 text-sm text-red-700/80">{{ useCase.error() }}</p>
            <button (click)="loadEntries()" class="mt-3 cursor-pointer text-sm font-semibold text-red-700 hover:underline">Intentar nuevamente</button>
          </div>
        </div>
      }

      @if (useCase.status() === 'loading' && useCase.data().length === 0) {
        <div class="flex flex-col items-center justify-center gap-4 p-12 text-[#6B7D6C]">
          <svg class="h-8 w-8 animate-spin text-[#2E7D32]" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p>Cargando asientos...</p>
        </div>
      }

      @if (useCase.status() === 'success' || (useCase.status() === 'loading' && useCase.data().length > 0)) {
        <div class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 sm:p-6 shadow-sm">
          <div class="overflow-x-auto">
            <table class="w-full text-sm text-left">
              <thead class="border-b border-[#E8F2E2] bg-[#F7FBF3] text-xs uppercase tracking-[0.12em] text-[#6B7D6C]">
                <tr>
                  <th scope="col" class="px-6 py-4 font-medium">Número</th>
                  <th scope="col" class="px-6 py-4 font-medium">Tipo</th>
                  <th scope="col" class="px-6 py-4 font-medium">Estado</th>
                  <th scope="col" class="px-6 py-4 font-medium">Débitos</th>
                  <th scope="col" class="px-6 py-4 font-medium">Créditos</th>
                  <th scope="col" class="px-6 py-4 font-medium">Publicado</th>
                  <th scope="col" class="px-6 py-4 font-medium text-right">Líneas</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-[#EEF5EA] bg-white">
                @for (entry of useCase.data(); track entry.id) {
                  <tr class="transition-colors hover:bg-[#F7FBF3]">
                    <td class="px-6 py-4 font-medium text-[#1B5E20]">
                      <div>{{ entry.entryNumber }}</div>
                      <div class="mt-1 text-xs text-[#6B7D6C]">{{ entry.reference || 'Sin referencia' }}</div>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ entry.entryType }}</td>
                    <td class="px-6 py-4">
                      <span class="px-2.5 py-1 text-xs font-semibold rounded-full"
                            [ngClass]="entry.status === 'POSTED' ? 'bg-green-500/10 text-green-600' : 'bg-slate-500/10 text-slate-600'">
                        {{ entry.status }}
                      </span>
                    </td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ formatAmount(entry.totalDebits) }}</td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ formatAmount(entry.totalCredits) }}</td>
                    <td class="px-6 py-4 text-[#4F5D4F]">{{ entry.postedAt ? (entry.postedAt | date:'medium') : 'Pendiente' }}</td>
                    <td class="px-6 py-4 text-right font-semibold text-[#2E7D32]">{{ entry.lines.length }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>

          <div class="mt-5">
            <app-platform-pagination
              [currentPage]="pageNumber()"
              [totalPages]="totalPages()"
              [totalElements]="totalElements()"
              [isLoading]="useCase.status() === 'loading'"
              (pageChange)="changePage($event)">
            </app-platform-pagination>
          </div>
        </div>
      }
    </div>
  `
})
export class AccountingJournalEntriesPageComponent implements OnInit {
  private readonly datePipe = inject(DatePipe);

  readonly useCase = inject(JournalEntryListUseCase);

  readonly pageNumber = computed(() => this.useCase.page()?.number ?? 0);
  readonly totalPages = computed(() => this.useCase.page()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.useCase.page()?.totalElements ?? 0);

  ngOnInit(): void {
    void this.loadEntries();
  }

  async loadEntries(page = 0): Promise<void> {
    await this.useCase.loadEntries(page, 20);
  }

  changePage(page: number): void {
    void this.loadEntries(page);
  }

  currentPageCount(): number {
    return this.useCase.data().length;
  }

  balancedCount(): number {
    return this.useCase.data().filter(entry => entry.balanced).length;
  }

  postedCount(): number {
    return this.useCase.data().filter(entry => entry.status === 'POSTED').length;
  }

  formatAmount(value: number): string {
    return new Intl.NumberFormat('es-BO', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  }

  formatDate(value: string | null | undefined): string {
    if (!value) {
      return 'N/A';
    }

    const formatted = this.datePipe.transform(value, 'medium');
    return formatted || 'N/A';
  }
}
