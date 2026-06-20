import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-service-payments-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Servicios / Tenant"
      title="Pagos de servicios"
      description="Ventanilla de pagos asistidos para admin tenant y owner admin. La fase siguiente agregará la interacción real."
      backLabel="Volver al dashboard"
      backRoute="/dashboard/summary"
      footerNote="Pago asistido"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class ServicePaymentsPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Proveedores', route: '/platform/service-providers', icon: 'building-2', description: 'Catálogo global de proveedores disponibles.' },
    { label: 'Deudas', route: '/platform/service-bills', icon: 'file-text', description: 'Consulta la deuda antes de registrar el pago.' },
    { label: 'Historial', route: '/dashboard/me/service-payments', icon: 'credit-card', description: 'Revisa el historial personal de pagos.' },
    { label: 'Mis servicios', route: '/dashboard/me/service-enrollments', icon: 'clipboard-list', description: 'Abre la gestión de servicios afiliados.' }
  ];
}
