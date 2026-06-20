import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-platform-service-providers-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Servicios / Plataforma"
      title="Proveedores de servicio"
      description="Administra el catálogo global de proveedores antes de que los tenants consuman o paguen sus deudas."
      backLabel="Volver a plataforma"
      backRoute="/platform/dashboard"
      footerNote="Catálogo global"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class PlatformServiceProvidersPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Clientes de servicio', route: '/platform/service-customers', icon: 'users', description: 'Relaciona códigos de servicio con clientes y contratos.' },
    { label: 'Deudas', route: '/platform/service-bills', icon: 'file-text', description: 'Visualiza y administra las cuentas pendientes por periodo.' },
    { label: 'Pagos globales', route: '/platform/service-bill-payments', icon: 'credit-card', description: 'Revisa el historial consolidado de pagos recibidos.' },
    { label: 'Panel', route: '/platform/dashboard', icon: 'layout-dashboard', description: 'Regresa al tablero principal de plataforma.' }
  ];
}
