import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-platform-service-bills-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Servicios / Plataforma"
      title="Deudas de servicio"
      description="Vista global de las deudas emitidas por los proveedores para cada tenant."
      backLabel="Volver a plataforma"
      backRoute="/platform/dashboard"
      footerNote="Deudas globales"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class PlatformServiceBillsPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Proveedores', route: '/platform/service-providers', icon: 'building-2', description: 'Consulta el catálogo de proveedores.' },
    { label: 'Clientes', route: '/platform/service-customers', icon: 'users', description: 'Administra códigos y vínculos de servicio.' },
    { label: 'Pagos globales', route: '/platform/service-bill-payments', icon: 'credit-card', description: 'Verifica pagos recibidos y referencias.' },
    { label: 'Auditoría', route: '/platform/audit', icon: 'clipboard-list', description: 'Revisa eventos recientes de plataforma.' }
  ];
}
