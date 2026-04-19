import { Component, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideLogOut, lucideSettings, lucideUser } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmDropdownMenuImports } from "@shared/ui/dropdown-menu";

@Component({
  selector: "app-user-menu",
  imports: [HlmButtonImports, HlmDropdownMenuImports, NgIcon],
  providers: [provideIcons({ lucideUser, lucideSettings, lucideLogOut })],
  templateUrl: "./user-menu.html",
  styleUrl: "./user-menu.css",
})
export class UserMenu {
  readonly userName = input<string>("User");
  readonly userEmail = input<string>("");
  readonly avatarUrl = input<string | null>(null);
  readonly ariaLabel = input<string>("Open user menu");

  readonly profileClick = output<void>();
  readonly settingsClick = output<void>();
  readonly logoutClick = output<void>();

  readonly DEFAULT_AVATAR = "https://i.pravatar.cc/40";
  imageError = false;

  get resolvedAvatar(): string {
    return this.avatarUrl() || this.DEFAULT_AVATAR;
  }

  get initials(): string {
    const name = this.userName().trim();
    if (!name) {
      return "U";
    }

    const parts = name.split(/\s+/).filter(Boolean);
    const first = parts[0]?.[0] ?? "";
    const second = parts[1]?.[0] ?? "";

    return (first + second).toUpperCase();
  }

  onProfileClick(): void {
    this.profileClick.emit();
  }

  onSettingsClick(): void {
    this.settingsClick.emit();
  }

  onLogoutClick(): void {
    this.logoutClick.emit();
  }
}
