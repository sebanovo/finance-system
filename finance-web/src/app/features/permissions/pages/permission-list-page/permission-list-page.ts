import { Component, inject, OnInit } from "@angular/core";
import { provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideInbox,
  lucideShieldCheck,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { CardHeader } from "../../../../shared/custom-components/card-header/card-header";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import { TableError } from "../../../../shared/custom-components/table-error/table-error";
import { PermissionTable } from "../../components/permission-table/permission-table";
import { PermissionsStore } from "../../store/permission.store";

@Component({
  selector: "app-permission-list-page",
  imports: [
    PermissionTable,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmEmptyImports,
    HlmSkeletonImports,
    CardHeader,
    TableError,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideInbox,
      lucideShieldCheck,
    }),
  ],
  templateUrl: "./permission-list-page.html",
  styleUrl: "./permission-list-page.css",
})
export class PermissionListPage implements OnInit {
  readonly store = inject(PermissionsStore);

  ngOnInit(): void {
    void this.store.loadPermissions();
  }

  onRetry(): void {
    void this.store.reloadPermissions();
  }
}
