import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { ReportColumn } from '../../../entities/reporting/model/reporting.model';

interface Slice {
  label: string;
  value: number;
  percent: number;
  color: string;
  dash: number;
  offset: number;
}

/** Dependency-free SVG donut chart: aggregates the first numeric column by the
 *  first label column (top 8 + "Otros"), with a centered total and a legend —
 *  executive dashboard look. */
@Component({
  selector: 'app-report-donut-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (slices().length > 0) {
      <div class="min-w-0 rounded-2xl border border-[#E8F2E2] bg-white p-5">
        <p class="mb-4 text-xs font-semibold uppercase tracking-[0.14em] text-[#6B7D6C]">
          {{ valueLabel() }} por {{ categoryLabel() }}
        </p>
        <div class="flex min-w-0 flex-col items-center gap-5 sm:flex-row">
          <div class="relative h-44 w-44 shrink-0">
            <svg viewBox="0 0 42 42" class="h-44 w-44 -rotate-90">
              <circle cx="21" cy="21" r="15.915" fill="transparent" stroke="#EEF5EA" stroke-width="4.5"></circle>
              @for (s of slices(); track s.label) {
                <circle cx="21" cy="21" r="15.915" fill="transparent"
                  [attr.stroke]="s.color" stroke-width="4.5" stroke-linecap="round"
                  [attr.stroke-dasharray]="s.dash + ' ' + (100 - s.dash)"
                  [attr.stroke-dashoffset]="s.offset"></circle>
              }
            </svg>
            <div class="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
              <span class="text-xl font-black leading-none text-[#1B5E20]">{{ formatNumber(total()) }}</span>
              <span class="mt-1 text-[10px] font-semibold uppercase tracking-wider text-[#9AA89A]">Total</span>
            </div>
          </div>
          <ul class="grid min-w-0 w-full grid-cols-1 gap-2 sm:grid-cols-2">
            @for (s of slices(); track s.label) {
              <li class="flex min-w-0 items-center gap-2 text-xs">
                <span class="h-3 w-3 shrink-0 rounded-full" [style.background-color]="s.color"></span>
                <span class="truncate text-[#4F5D4F]" [title]="s.label">{{ s.label }}</span>
                <span class="ml-auto shrink-0 font-semibold text-[#1B5E20]">{{ formatNumber(s.value) }}</span>
                <span class="w-9 shrink-0 text-right text-[10px] text-[#9AA89A]">{{ s.percent }}%</span>
              </li>
            }
          </ul>
        </div>
      </div>
    }
  `
})
export class ReportDonutChartComponent {
  readonly columns = input<ReportColumn[]>([]);
  readonly rows = input<unknown[][]>([]);

  private readonly palette = [
    '#2E7D32', '#4CAF50', '#1E88E5', '#FB8C00', '#E53935',
    '#8E24AA', '#00897B', '#FDD835', '#6D4C41', '#9E9E9E'
  ];

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

  private readonly aggregated = computed<[string, number][]>(() => {
    const catIdx = this.categoryIndex();
    const valIdx = this.valueIndex();
    if (catIdx < 0 || valIdx < 0) return [];

    const totals = new Map<string, number>();
    for (const row of this.rows()) {
      const label = String(row[catIdx] ?? '—');
      const value = Math.abs(Number(row[valIdx] ?? 0));
      if (!Number.isFinite(value)) continue;
      totals.set(label, (totals.get(label) ?? 0) + value);
    }
    let entries = [...totals.entries()].sort((a, b) => b[1] - a[1]);
    if (entries.length > 9) {
      const top = entries.slice(0, 8);
      const rest = entries.slice(8).reduce((sum, [, v]) => sum + v, 0);
      entries = [...top, ['Otros', rest] as [string, number]];
    }
    return entries;
  });

  readonly total = computed(() => this.aggregated().reduce((sum, [, v]) => sum + v, 0));

  readonly slices = computed<Slice[]>(() => {
    const entries = this.aggregated();
    const total = this.total();
    if (total <= 0) return [];

    const gap = entries.length > 1 ? 1 : 0; // small visual separation between segments
    let offset = 25; // start at the top (12 o'clock) for the -90deg rotation
    return entries.map(([label, value], i) => {
      const fraction = (value / total) * 100;
      const slice: Slice = {
        label,
        value,
        percent: Math.round(fraction),
        color: this.palette[i % this.palette.length],
        dash: Math.max(0, fraction - gap),
        offset
      };
      offset = (offset - fraction + 100) % 100;
      return slice;
    });
  });

  formatNumber(value: number): string {
    return new Intl.NumberFormat('es-BO', { maximumFractionDigits: 2 }).format(value);
  }
}
