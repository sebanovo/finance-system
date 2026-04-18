import { Component, effect, inject, input, output } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideLoaderCircle,
  lucideShield,
  lucideUserCog,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCheckboxImports } from "@shared/ui/checkbox";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSheetImports } from "@shared/ui/sheet";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { RolesStore } from "../../../roles/store/role.store";
import { UserRoleAssignmentUserContext } from "../../model/role-assignment.type";
import { UserRoleAssignmentStore } from "../../store/role-assignment.store";

@Component({
  selector: "app-manage-role-drawer",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCheckboxImports,
    HlmEmptyImports,
    HlmSheetImports,
    HlmSkeletonImports,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideLoaderCircle,
      lucideShield,
      lucideUserCog,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./manage-role-drawer.html",
  styleUrl: "./manage-role-drawer.css",
})
export class ManageRoleDrawer {
  readonly store = inject(UserRoleAssignmentStore);
  readonly rolesStore = inject(RolesStore);

  readonly open = input.required<boolean>();
  readonly user = input<UserRoleAssignmentUserContext | null>(null);

  readonly openChange = output<boolean>();
  readonly saved = output<void>();

  constructor() {
    effect(() => {
      const isOpen = this.open();
      const user = this.user();

      if (!isOpen || !user) {
        return;
      }

      void this.store.loadForUser(user.id);
    });
  }

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);

    if (!isOpen) {
      this.store.resetState();
    }
  }

  onClosed(): void {
    this.openChange.emit(false);
    this.store.resetState();
  }

  onToggleRole(roleId: string, checked: boolean): void {
    this.store.toggleRole(roleId, checked);
  }

  async onSave(): Promise<void> {
    const user = this.user();

    if (!user) {
      return;
    }

    const success = await this.store.save(user.id);

    if (!success) {
      toast("No se pudieron guardar los roles", {
        description:
          this.store.submitError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast("Roles actualizados correctamente", {
      description: `Los roles de ${user.firstName} ${user.lastName} fueron actualizados.`,
    });

    this.saved.emit();
    this.onClosed();
  }

  onCancel(): void {
    this.onClosed();
  }
}
