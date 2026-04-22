import { Component, input, output, signal, effect, ElementRef, viewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { PlatformTenantResponse } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-tenant-detail-dialog',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './tenant-detail-dialog.component.html',
})
export class TenantDetailDialogComponent {
  tenant = input<PlatformTenantResponse | null>(null);
  close = output<void>();

  private readonly dialogRef = viewChild<ElementRef<HTMLDialogElement>>('dialogEl');

  constructor() {
    effect(() => {
      const dialog = this.dialogRef()?.nativeElement;
      if (!dialog) return;

      if (this.tenant()) {
        dialog.showModal();
      } else {
        dialog.close();
      }
    });
  }

  onClose(): void {
    this.close.emit();
  }
}
