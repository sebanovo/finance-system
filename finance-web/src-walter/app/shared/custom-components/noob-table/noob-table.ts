import { Component, OnInit, TemplateRef, viewChild } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEye, lucidePencil, lucideTrash2 } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmSwitchImports } from "@shared/ui/switch";
import { Column, TableBase } from "../table-base/table-base";

type User = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  isActive: boolean;
};

@Component({
  selector: "app-noob-table",
  imports: [TableBase, HlmButtonImports, NgIcon, HlmSwitchImports],
  providers: [provideIcons({ lucideTrash2, lucideEye, lucidePencil })],
  templateUrl: "./noob-table.html",
  styleUrl: "./noob-table.css",
})
export class NoobTable implements OnInit {
  protected readonly actionsTemplate =
    viewChild.required<TemplateRef<{ $implicit: User }>>("actionsTemplate");

  data: User[] = [
    {
      id: 1,
      firstName: "Walter",
      lastName: "Orellana",
      email: "walter@example.com",
      isActive: true,
    },
    {
      id: 2,
      firstName: "Maria",
      lastName: "Perez",
      email: "maria@example.com",
      isActive: false,
    },
  ];

  columns: Column<User>[] = [];

  ngOnInit() {
    this.columns = [
      { key: "id", label: "ID" },
      { key: "firstName", label: "Nombre" },
      { key: "lastName", label: "Apellido" },
      { key: "email", label: "Email" },
      { key: "isActive", label: "Activo" },
      { label: "Acciones", template: this.actionsTemplate() },
    ];
  }

  edit(user: User) {
    console.log("Editar", user);
  }

  delete(user: User) {
    console.log("Eliminar", user);
  }
}
