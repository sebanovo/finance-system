import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-table.component.html'
})
export class UserTableComponent {
  // Componente puramente presentacional
  @Input() users: TenantUserResponse[] = [];
}
