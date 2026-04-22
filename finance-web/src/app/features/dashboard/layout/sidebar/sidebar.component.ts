import { Component, input, output, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { SessionStore } from '../../../../core/session/store/session.store';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
})
export class SidebarComponent {
  private readonly sessionStore = inject(SessionStore);

  /** Whether the sidebar is open */
  isOpen = input<boolean>(true);

  /** Emits when sidebar should close (mobile) */
  closeSidebar = output<void>();

  /** User initials for avatar */
  userInitials = computed(() => {
    const user = this.sessionStore.user();
    if (!user) return 'US';
    const first = user.firstName?.charAt(0) ?? '';
    const last = user.lastName?.charAt(0) ?? '';
    return (first + last).toUpperCase() || 'US';
  });

  /** User display name */
  userName = computed(() => this.sessionStore.userFullName() ?? 'Usuario');

  /** User email */
  userEmail = computed(() => this.sessionStore.user()?.email ?? '');

  onNavigate(): void {
    if (window.innerWidth < 768) {
      this.closeSidebar.emit();
    }
  }
}
