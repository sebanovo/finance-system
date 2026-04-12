import { Component, computed, inject, signal } from "@angular/core";
import { TenantService } from "../../services/tenant.service";

@Component({
  selector: "app-tenant-list-page",
  imports: [],
  templateUrl: "./tenant-list-page.html",
  styleUrl: "./tenant-list-page.css",
})
export class TenantListPage {
  private service = inject(TenantService);

  readonly tenants = signal<any[]>([]);
  readonly loading = signal(true);
  readonly search = signal("");

  constructor() {
    this.loadTenants();
  }

  loadTenants() {
    this.loading.set(true);

    this.service.getTenants().subscribe({
      next: data => {
        this.tenants.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.tenants.set([]);
        this.loading.set(false);
      },
    });
  }

  readonly filteredTenants = computed(() => {
    const term = this.search().toLowerCase();

    return this.tenants().filter(t => t.name.toLowerCase().includes(term));
  });

  onSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.search.set(value);
  }
}
