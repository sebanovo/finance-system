import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PlatformAuthStore } from '../auth/store/platform-auth.store';
import { HeaderComponent } from './header/header.component';
import { SidebarComponent } from './sidebar/sidebar.component';

@Component({
  selector: 'platform-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent,
    SidebarComponent
  ],
  templateUrl: './platform-layout.component.html',
})
export class PlatformLayoutComponent implements OnInit {
  readonly store = inject(PlatformAuthStore);

  isSidebarOpen = signal<boolean>(true);

  ngOnInit(): void {
    this.store.loadMe();
  }

  toggleSidebar() {
    this.isSidebarOpen.update(state => !state);
  }

  onLogout(): void {
    this.store.logout();
  }
}