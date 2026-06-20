import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-platform-service-customers-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Servicios / Plataforma"
      title="Clientes de servicio"
      description="Gestiona la relación entre proveedores, códigos de servicio y clientes globales."
      backLabel="Volver a plataforma"
      backRoute="/platform/dashboard"
      footerNote="Clientes globales"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class PlatformServiceCustomersPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Proveedores', route: '/platform/service-providers', icon: 'building-2', description: 'Consulta el catálogo de proveedores activos.' },
    { label: 'Deudas', route: '/platform/service-bills', icon: 'file-text', description: 'Revisa los saldos generados por periodo.' },
    { label: 'Pagos globales', route: '/platform/service-bill-payments', icon: 'credit-card', description: 'Audita el historial consolidado de pagos.' },
    { label: 'Reportes', route: '/platform/reporting', icon: 'bar-chart-3', description: 'Vuelve al espacio analítico de la plataforma.' }
  ];
}
