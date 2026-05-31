// pages/platform-audit-page/platform-audit-page.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { PlatformAuditListUseCase } from '../../features/platform/application/platform-audit-list.usecase';
import { PlatformAuditTableComponent } from '../../features/platform/ui/platform-audit-table/platform-audit-table.component';
import { AuditEvent } from '../../entities/platform/api/platform.service';

@Component({
  selector: 'app-platform-audit-page',
  standalone: true,
  imports: [CommonModule, LucideAngularModule, PlatformAuditTableComponent],
  template: `
    <div class="p-6">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-[#2E7D32]">Auditoría de la Plataforma</h1>
        <p class="text-sm text-[#666666] mt-1">Registro de eventos y acciones del sistema</p>
      </div>

      <app-platform-audit-table
        [events]="auditEvents"
        [isLoading]="isLoading">
      </app-platform-audit-table>
    </div>
  `
})
export class PlatformAuditPageComponent implements OnInit {
  private listUseCase = inject(PlatformAuditListUseCase);
  private cdr = inject(ChangeDetectorRef);

  // ✅ Getters con tipos explícitos
  get auditEvents(): AuditEvent[] {
    return this.listUseCase.events();
  }

  get isLoading(): boolean {
    return this.listUseCase.status() === 'loading';
  }

  ngOnInit(): void {
    this.loadAuditEvents();
  }

  async loadAuditEvents(): Promise<void> {
    await this.listUseCase.loadAuditEvents(50);
    this.cdr.detectChanges();
  }
}