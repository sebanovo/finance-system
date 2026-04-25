import { Component, computed, inject, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideLoaderCircle,
  lucideUserPen,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { UserForm } from "../../components/user-form/user-form";
import { UserUpsertFormValue } from "../../models/user.model";
import { UsersStore } from "../../store/user.store";

@Component({
  selector: "app-user-edit-page",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    UserForm,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideLoaderCircle,
      lucideUserPen,
    }),
  ],
  templateUrl: "./user-edit-page.html",
  styleUrl: "./user-edit-page.css",
})
export class UserEditPage implements OnInit {
  readonly store = inject(UsersStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private userId = "";

  async ngOnInit(): Promise<void> {
    this.store.clearSelectedUserError();
    this.store.clearUpdateError();
    this.store.clearSelectedUser();

    this.userId = this.route.snapshot.paramMap.get("id") ?? "";

    if (!this.userId) {
      void this.router.navigate(["/app/users"]);
      return;
    }

    const user = await this.store.loadUserById(this.userId);

    if (!user) {
      toast("No se pudo cargar el usuario", {
        description:
          this.store.selectedUserError()?.message ||
          "Ocurrió un error inesperado.",
      });
    }
  }

  async onUpdateUser(formValue: UserUpsertFormValue): Promise<void> {
    this.store.clearUpdateError();

    const updatedUser = await this.store.updateUser(this.userId, formValue);

    if (!updatedUser) {
      toast("No se pudo actualizar el usuario", {
        description:
          this.store.updateError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast("Usuario actualizado correctamente", {
      description: `${updatedUser.firstName} ${updatedUser.lastName} fue actualizado con éxito.`,
      action: {
        label: "Undo",
        onClick: () => console.log("Undo update user"),
      },
    });

    void this.router.navigate(["/app/users"]);
  }

  onCancel(): void {
    this.store.clearUpdateError();
    void this.router.navigate(["/app/users"]);
  }

  readonly initialFormValue = computed<UserUpsertFormValue | null>(() => {
    const user = this.store.selectedUser();

    if (!user) {
      return null;
    }

    return {
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      password: "************",
    };
  });
}
