import { Component, inject, OnInit, signal } from "@angular/core";
import { Router } from "@angular/router";
import { provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucidePlus,
  lucideUsers,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { CardHeader } from "../../../../shared/custom-components/card-header/card-header";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { TableError } from "../../../../shared/custom-components/table-error/table-error";
import { UserDetailDialog } from "../../components/user-detail-dialog/user-detail-dialog";
import { UserTable } from "../../components/user-table/user-table";
import { User } from "../../models/user.model";
import { UsersStore } from "../../store/user.store";

@Component({
  selector: "app-user-list-page",
  imports: [
    UserTable,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    UserDetailDialog,
    CardHeader,
    TableError,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucidePlus,
      lucideUsers,
    }),
  ],
  templateUrl: "./user-list-page.html",
  styleUrl: "./user-list-page.css",
})
export class UserListPage implements OnInit {
  readonly store = inject(UsersStore);
  private readonly router = inject(Router);
  readonly viewDialogOpen = signal(false);

  ngOnInit(): void {
    console.log("init", this.store.users);
    void this.store.loadUsers();
  }

  onRetry(): void {
    void this.store.reloadUsers();
  }

  onCreateUser(): void {
    void this.router.navigate(["/app", "users", "create"]);
  }

  onEditUser(userId: string): void {
    void this.router.navigate(["/app", "users", userId, "edit"]);
  }

  async onActivateUser(user: User): Promise<void> {
    await this.handleToggleUser(user);
  }

  async onDeactivateUser(user: User): Promise<void> {
    await this.handleToggleUser(user);
  }

  private async handleToggleUser(user: User): Promise<void> {
    this.store.clearToggleError();

    // keep state

    const updatedUser = await this.store.toggleUserActiveState(user);
    console.log(this.store.toggleError());

    if (!updatedUser) {
      toast("No se pudo actualizar el estado del usuario", {
        description:
          this.store.toggleError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast(
      updatedUser.isActive
        ? "Usuario activado correctamente"
        : "Usuario desactivado correctamente",
      {
        description: `${updatedUser.firstName} ${updatedUser.lastName} fue actualizado.`,
        action: {
          label: "Undo",
          onClick: () => console.log("Undo toggle user"),
        },
      }
    );
  }

  async onViewUser(user: User): Promise<void> {
    this.store.clearSelectedUserError();
    this.store.clearSelectedUser();
    this.viewDialogOpen.set(true);

    const loadedUser = await this.store.loadUserById(user.id);

    if (!loadedUser) {
      toast("No se pudo cargar el usuario", {
        description:
          this.store.selectedUserError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  onViewDialogOpenChange(isOpen: boolean): void {
    this.viewDialogOpen.set(isOpen);

    if (!isOpen) {
      this.store.clearSelectedUserError();
      this.store.clearSelectedUser();
    }
  }
}
