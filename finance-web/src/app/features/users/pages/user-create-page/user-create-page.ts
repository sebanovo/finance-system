import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { HlmButtonImports } from "@shared/ui/button";
import { toast } from "@spartan-ng/brain/sonner";
import { UserForm } from "../../components/user-form/user-form";
import { UserUpsertFormValue } from "../../models/user.model";
import { UsersStore } from "../../store/user.store";

@Component({
  selector: "app-user-create-page",
  imports: [HlmButtonImports, UserForm],
  templateUrl: "./user-create-page.html",
  styleUrl: "./user-create-page.css",
})
export class UserCreatePage implements OnInit {
  readonly store = inject(UsersStore);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.store.clearCreateError();
  }

  async onCreateUser(formValue: UserUpsertFormValue): Promise<void> {
    this.store.clearCreateError();

    const createdUser = await this.store.createUser(formValue);

    if (!createdUser) {
      const errorMessage =
        this.store.createError()?.message || "No se pudo crear el usuario.";

      toast("No se pudo crear el usuario", {
        description: errorMessage,
      });

      return;
    }

    toast("Usuario creado correctamente", {
      description: `${createdUser.firstName} ${createdUser.lastName} fue registrado con éxito.`,
      action: {
        label: "Undo",
        onClick: () => console.log("Undo create user"),
      },
    });

    void this.router.navigate(["/app/users"]);
  }

  onCancel(): void {
    this.store.clearCreateError();
    void this.router.navigate(["/app", "users"]);
  }
}
