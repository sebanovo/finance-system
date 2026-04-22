import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { PlatformTenantResponse } from '../../models/platform-tenant.models';

@Component({
  selector: 'app-tenant-table',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './tenant-table.component.html'
})
export class TenantTableComponent {
  tenants = input<PlatformTenantResponse[]>([]);
  isLoading = input<boolean>(false);

  toggleStatus = output<PlatformTenantResponse>();
  viewDetails = output<string>();
}