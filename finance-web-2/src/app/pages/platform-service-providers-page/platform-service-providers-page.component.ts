import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import {
  PlatformServiceProviderFormComponent,
  PlatformServiceProviderListUseCase,
  PlatformServiceProviderTableComponent
} from '../../features/service-payments';
import { PlatformPaginationComponent } from '../../features/platform/ui/platform-pagination/platform-pagination.component';
import { ServiceProviderResponse } from '../../entities/service-payments';
import { ToastService } from '../../shared/ui/toast/toast.service';

@Component({
  selector: 'app-platform-service-providers-page',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    PlatformPaginationComponent,
    PlatformServiceProviderFormComponent,
    PlatformServiceProviderTableComponent
  ],
  template: `
    <section class="space-y-6">
      <div class="rounded-[28px] border border-[#C8E6C9] bg-white p-6 shadow-sm">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div class="flex items-start gap-4">
            <div class="rounded-2xl bg-[#F1F8E9] p-3 text-[#2E7D32]">
              <lucide-icon name="building" class="h-6 w-6"></lucide-icon>
            </div>
            <div>
              <h1 class="text-2xl font-black tracking-tight text-[#1B5E20]">Proveedores de servicios</h1>
              <p class="mt-1 text-sm text-[#6B7D6C]">
                Administra empresas de luz, agua, internet y cable disponibles para pagos.
              </p>
            </div>
          </div>

          <button
            type="button"
            (click)="openCreate()"
            class="inline-flex cursor-pointer items-center justify-center gap-2 rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428]">
            <lucide-icon name="plus" class="h-4 w-4"></lucide-icon>
            Nuevo proveedor
          </button>
        </div>
      </div>

      <section class="rounded-[24px] border border-[#C8E6C9] bg-white p-4 shadow-sm sm:p-6">
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-lg font-bold text-[#1B5E20]">Filtros</h2>
            <p class="text-sm text-[#6B7D6C]">Filtra por categoría, estado o texto de búsqueda.</p>
          </div>

          <div class="grid w-full gap-3 md:grid-cols-2 xl:grid-cols-5">
            <input
              type="text"
              [value]="search()"
              (input)="search.set($any($event.target).value)"
              placeholder="Buscar proveedor"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none placeholder:text-[#9AA99A] focus:border-[#2E7D32] focus:bg-white" />

            <select
              [value]="category()"
              (change)="category.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todas las categorías</option>
              <option value="ELECTRICITY">Luz</option>
              <option value="WATER">Agua</option>
              <option value="INTERNET">Internet</option>
              <option value="TV_CABLE">Cable TV</option>
            </select>

            <select
              [value]="status()"
              (change)="status.set($any($event.target).value)"
              class="h-11 w-full min-w-0 rounded-2xl border border-[#DDEED8] bg-[#FAFCF8] px-4 text-sm text-[#1B5E20] outline-none focus:border-[#2E7D32] focus:bg-white">
              <option value="">Todos los estados</option>
              <option value="ACTIVE">Activos</option>
              <option value="INACTIVE">Inactivos</option>
            </select>

            <button
              type="button"
              (click)="applyFilters()"
              class="cursor-pointer rounded-full bg-[#2E7D32] px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-[#256428] md:col-span-2 xl:col-span-1">
              Aplicar
            </button>
          </div>
        </div>
      </section>

      <app-platform-service-provider-table
        [providers]="useCase.data()"
        (edit)="openEdit($event)"
        (toggleStatus)="toggleStatus($event)">
      </app-platform-service-provider-table>

      @if (useCase.page(); as page) {
        <app-platform-pagination
          [currentPage]="page.number"
          [totalPages]="page.totalPages"
          [totalElements]="page.totalElements"
          [isLoading]="useCase.status() === 'loading'"
          (pageChange)="changePage($event)">
        </app-platform-pagination>
      }

      <app-platform-service-provider-form
        [isOpen]="formOpen()"
        [provider]="selectedProvider()"
        [isSubmitting]="submitting()"
        (closed)="closeForm()"
        (saved)="saveProvider($event)">
      </app-platform-service-provider-form>
    </section>
  `
})
export class PlatformServiceProvidersPageComponent implements OnInit {
  readonly useCase = inject(PlatformServiceProviderListUseCase);
  private readonly toast = inject(ToastService);

  readonly formOpen = signal(false);
  readonly submitting = signal(false);
  readonly selectedProvider = signal<ServiceProviderResponse | null>(null);

  readonly search = signal('');
  readonly category = signal('');
  readonly status = signal('');

  ngOnInit(): void {
    void this.useCase.loadProviders();
  }

  changePage(page: number): void {
    void this.useCase.loadProviders(page);
  }

  applyFilters(): void {
    void this.useCase.applyFilter({
      search: this.search().trim() || undefined,
      category: this.category() || undefined,
      status: this.status() || undefined
    });
  }

  openCreate(): void {
    this.selectedProvider.set(null);
    this.formOpen.set(true);
  }

  openEdit(provider: ServiceProviderResponse): void {
    this.selectedProvider.set(provider);
    this.formOpen.set(true);
  }

  closeForm(): void {
    this.formOpen.set(false);
    this.selectedProvider.set(null);
    this.submitting.set(false);
  }

  async saveProvider(event: any): Promise<void> {
    this.submitting.set(true);
    try {
      if (event.isEditing && event.id) {
        await this.useCase.updateProvider(event.id, event.request);
        this.toast.success('Proveedor actualizado correctamente');
      } else {
        await this.useCase.createProvider(event.request);
        this.toast.success('Proveedor creado correctamente');
      }
      this.closeForm();
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo guardar el proveedor');
      this.submitting.set(false);
    }
  }

  async toggleStatus(provider: ServiceProviderResponse): Promise<void> {
    try {
      await this.useCase.changeProviderStatus(provider.id, {
        status: provider.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      });
      this.toast.success('Estado del proveedor actualizado');
    } catch (error: any) {
      this.toast.error(error.message || 'No se pudo cambiar el estado');
    }
  }
}
