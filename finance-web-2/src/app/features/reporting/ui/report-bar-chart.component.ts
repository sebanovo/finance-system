import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { ReportColumn } from '../../../entities/reporting/model/reporting.model';

interface Bar {
  label: string;
  value: number;
  percent: number;
}

/** Lightweight, dependency-free bar chart: first text/date column as label,
 *  first numeric column as value. The frontend owns charting (no backend metadata). */
@Component({
  selector: 'app-report-bar-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (bars().length > 0) {
      <div class="space-y-2">
        <p class="text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">
          {{ valueLabel() }} por {{ categoryLabel() }}
        </p>
        @for (bar of bars(); track bar.label) {
          <div class="flex items-center gap-3">
            <span class="w-40 shrink-0 truncate text-xs text-[#4F5D4F]" [title]="bar.label">{{ bar.label }}</span>
            <div class="h-5 flex-1 overflow-hidden rounded-full bg-[#EEF5EA]">
              <div class="h-full rounded-full bg-gradient-to-r from-[#4CAF50] to-[#2E7D32]"
                   [style.width.%]="bar.percent"></div>
            </div>
            <span class="w-24 shrink-0 text-right text-xs font-semibold text-[#1B5E20]">
              {{ formatNumber(bar.value) }}
            </span>
          </div>
        }
      </div>
    }
  `
})
export class ReportBarChartComponent {
  readonly columns = input<ReportColumn[]>([]);
  readonly rows = input<unknown[][]>([]);

  private readonly categoryIndex = computed(() =>
    this.columns().findIndex(c => c.type === 'TEXT' || c.type === 'DATE' || c.type === 'TIMESTAMP'));

  private readonly valueIndex = computed(() =>
    this.columns().findIndex(c => c.type === 'NUMBER'));

  readonly categoryLabel = computed(() => {
    const i = this.categoryIndex();
    return i >= 0 ? this.columns()[i].name : 'categoría';
  });

  readonly valueLabel = computed(() => {
    const i = this.valueIndex();
    return i >= 0 ? this.columns()[i].name : 'valor';
  });

  readonly bars = computed<Bar[]>(() => {
    const catIdx = this.categoryIndex();
    const valIdx = this.valueIndex();
    if (catIdx < 0 || valIdx < 0) {
      return [];
    }
    const raw = this.rows()
      .slice(0, 12)
      .map(row => ({ label: String(row[catIdx] ?? '—'), value: Number(row[valIdx] ?? 0) }))
      .filter(b => Number.isFinite(b.value));
    const max = Math.max(...raw.map(b => Math.abs(b.value)), 1);
    return raw.map(b => ({ ...b, percent: Math.max(2, Math.round((Math.abs(b.value) / max) * 100)) }));
  });

  formatNumber(value: number): string {
    return new Intl.NumberFormat('es-BO').format(value);
  }
}
