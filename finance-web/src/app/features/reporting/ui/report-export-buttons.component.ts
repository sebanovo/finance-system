import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

/** PDF / Excel export buttons. Emits the chosen format; the page handles the call. */
@Component({
  selector: 'app-report-export-buttons',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-wrap gap-2">
      <button type="button" [disabled]="disabled() || busy()" (click)="exportFormat.emit('PDF')"
        class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:cursor-not-allowed disabled:opacity-50">
        Exportar PDF
      </button>
      <button type="button" [disabled]="disabled() || busy()" (click)="exportFormat.emit('XLSX')"
        class="cursor-pointer rounded-full border border-[#C8E6C9] bg-white px-4 py-2 text-sm font-semibold text-[#2E7D32] transition-colors hover:bg-[#F1F8E9] disabled:cursor-not-allowed disabled:opacity-50">
        Exportar Excel
      </button>
    </div>
  `
})
export class ReportExportButtonsComponent {
  readonly disabled = input<boolean>(false);
  readonly busy = input<boolean>(false);
  readonly exportFormat = output<'PDF' | 'XLSX'>();
}
