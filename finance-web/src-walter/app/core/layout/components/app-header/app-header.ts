import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  signal,
} from "@angular/core";
import { Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBell,
  lucideMenu,
  lucideSearch,
  lucideUser,
} from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmInputImports } from "@shared/ui/input";
import { AuthStore } from "../../../../features/auth/store/auth.store";
import { ChangePasswordDialog } from "../../../../features/password/components/change-password-dialog/change-password-dialog";
import { ChangePasswordFormValue } from "../../../../features/password/model/password.type";
import { PasswordStore } from "../../../../features/password/store/password.store";
import { SessionStore } from "../../../session/store/session.store";
import { AppLayoutState } from "../../services/app-layout.state";
import { UserMenu } from "../user-menu/user-menu";

@Component({
  selector: "app-app-header",
  standalone: true,
  imports: [
    NgIcon,
    HlmButtonImports,
    HlmInputImports,
    UserMenu,
    ChangePasswordDialog,
  ],
  providers: [
    provideIcons({
      lucideBell,
      lucideSearch,
      lucideUser,
      lucideMenu,
    }),
  ],
  templateUrl: "./app-header.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppHeader {
  private readonly sessionStore = inject(SessionStore);
  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);

  readonly title = input("Dashboard");
  readonly subtitle = input("Administración");
  readonly showSearch = input(true);
  readonly showNotifications = input(true);
  readonly showProfile = input(true);
  readonly searchPlaceholder = input("Buscar...");

  protected readonly layoutState = inject(AppLayoutState);
  readonly currentUser = this.sessionStore.user;

  readonly passwordStore = inject(PasswordStore);
  readonly changePasswordDialogOpen = signal(false);

  readonly userName = computed(() => {
    const user = this.currentUser();

    if (!user) {
      return "User";
    }

    return `${user.firstName} ${user.lastName}`;
  });

  readonly userEmail = computed(() => this.currentUser()?.email ?? "");

  readonly avatarUrl = computed(() => {
    const user = this.currentUser();
    return user ? null : null;
  });

  protected openMobileSidebar(): void {
    this.layoutState.openMobileSidebar();
  }

  onClickPerfil(): void {
    // this.router.navigate(["/profile"]);
    console.log("Profile click");
  }

  onClickSettings(): void {
    // this.router.navigate(["/settings"]);
    console.log("Settings  click");
    this.router.navigate(["/app/settings"]);
  }

  protected onClickLogout(): void {
    this.sessionStore.clearSession();
    this.authStore.logout();
  }

  onClickChangePassword(): void {
    this.changePasswordDialogOpen.set(true);
    this.passwordStore.clearChangeState();
  }

  onChangePasswordDialogOpenChange(isOpen: boolean): void {
    this.changePasswordDialogOpen.set(isOpen);

    if (!isOpen) {
      this.passwordStore.clearChangeState();
    }
  }

  async onSubmitChangePassword(
    payload: ChangePasswordFormValue
  ): Promise<void> {
    const success = await this.passwordStore.changePassword(payload);

    if (!success) {
      return;
    }

    this.changePasswordDialogOpen.set(false);
    this.passwordStore.clearChangeState();
  }
}
