import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { ReportColumn } from '../../../entities/reporting/model/reporting.model';

/** Generic result table: renders typed columns + rows. No per-report logic. */
@Component({
  selector: 'app-report-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (columns().length === 0) {
      <div class="rounded-2xl border border-dashed border-[#C8E6C9] bg-[#FAFCF8] p-6 text-sm text-[#5F6F5F]">
        Sin columnas para mostrar.
      </div>
    } @else {
      <div class="overflow-x-auto rounded-2xl border border-[#E8F2E2]">
        <table class="w-full divide-y divide-[#E8F2E2]">
          <thead class="bg-[#F7FBF3]">
            <tr class="text-left text-xs font-bold uppercase tracking-[0.12em] text-[#6B7D6C]">
              @for (col of columns(); track col.name) {
                <th class="px-4 py-3 whitespace-nowrap">{{ col.name }}</th>
              }
            </tr>
          </thead>
          <tbody class="divide-y divide-[#EEF5EA] bg-white">
            @for (row of rows(); track $index) {
              <tr class="hover:bg-[#FAFCF8]">
                @for (col of columns(); track col.name; let i = $index) {
                  <td class="px-4 py-3 text-sm text-[#4F5D4F] whitespace-nowrap"
                      [class.font-semibold]="col.type === 'NUMBER'"
                      [class.text-[#1B5E20]]="col.type === 'NUMBER'">
                    {{ format(row[i], col.type) }}
                  </td>
                }
              </tr>
            } @empty {
              <tr>
                <td [attr.colspan]="columns().length" class="px-4 py-6 text-center text-sm text-[#6B7D6C]">
                  El reporte no devolvió filas.
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    }
  `
})
export class ReportTableComponent {
  readonly columns = input<ReportColumn[]>([]);
  readonly rows = input<unknown[][]>([]);

  format(value: unknown, type: string): string {
    if (value === null || value === undefined || value === '') {
      return '—';
    }
    if (type === 'NUMBER') {
      const num = Number(value);
      return Number.isFinite(num) ? new Intl.NumberFormat('es-BO').format(num) : String(value);
    }
    if (type === 'BOOLEAN') {
      return value === true || value === 'true' ? 'Sí' : 'No';
    }
    return String(value);
  }
}
