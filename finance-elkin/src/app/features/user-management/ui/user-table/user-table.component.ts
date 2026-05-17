import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TenantUserResponse } from '../../../../entities/user';

@Component({
  selector: 'app-user-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-table.component.html'
})
export class UserTableComponent {
  @Input() users: TenantUserResponse[] = [];
  @Output() manageRoles = new EventEmitter<TenantUserResponse>();

  onManageRoles(user: TenantUserResponse): void {
    this.manageRoles.emit(user);
  }
}
