import { Component, output, input, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../../features/auth/services/auth.service';
import { SessionStore } from '../../../../core/session/store/session.store';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);
  private readonly sessionStore = inject(SessionStore);
  private readonly router = inject(Router);

  /** Whether the sidebar is currently open */
  sidebarOpen = input<boolean>(true);

  /** Emits when the sidebar toggle button is clicked */
  toggleSidebar = output<void>();

  /** Whether the profile dropdown is open */
  profileMenuOpen = signal(false);

  /** User initials for the avatar badge */
  get userInitials(): string {
    const user = this.sessionStore.user();
    if (!user) return 'US';
    const first = user.firstName?.charAt(0) ?? '';
    const last = user.lastName?.charAt(0) ?? '';
    return (first + last).toUpperCase() || 'US';
  }

  /** User full name */
  get userName(): string {
    return this.sessionStore.userFullName() ?? 'Usuario';
  }

  /** User email */
  get userEmail(): string {
    return this.sessionStore.user()?.email ?? '';
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  toggleProfileMenu(): void {
    this.profileMenuOpen.update(v => !v);
  }

  closeProfileMenu(): void {
    this.profileMenuOpen.set(false);
  }

  onLogout(): void {
    this.authService.logout();
    this.sessionStore.clearSession();
    this.closeProfileMenu();
    this.router.navigate(['/auth/login']);
  }
}