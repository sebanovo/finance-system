import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-platform-service-bill-payments-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Servicios / Plataforma"
      title="Pagos globales"
      description="Consulta los pagos de servicio consolidados, útiles para supervisión y soporte operativo."
      backLabel="Volver a plataforma"
      backRoute="/platform/dashboard"
      footerNote="Seguimiento global"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class PlatformServiceBillPaymentsPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Deudas', route: '/platform/service-bills', icon: 'file-text', description: 'Verifica el origen de cada pago registrado.' },
    { label: 'Proveedores', route: '/platform/service-providers', icon: 'building-2', description: 'Regresa al catálogo de proveedores.' },
    { label: 'Clientes', route: '/platform/service-customers', icon: 'users', description: 'Explora la relación cliente-código de servicio.' },
    { label: 'Planes', route: '/platform/plans', icon: 'credit-card', description: 'Vuelve al módulo de planes de la plataforma.' }
  ];
}
