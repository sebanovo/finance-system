import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-my-service-enrollments-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Mis servicios"
      title="Servicios afiliados"
      description="Administra los servicios que el cliente tiene vinculados a su cuenta. El alta y baja real llegará en la siguiente fase."
      backLabel="Volver a mi dashboard"
      backRoute="/dashboard/me/summary"
      footerNote="Afiliaciones"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class MyServiceEnrollmentsPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Historial de pagos', route: '/dashboard/me/service-payments', icon: 'credit-card', description: 'Ver pagos realizados desde tu cuenta.' },
    { label: 'Mis cuentas', route: '/dashboard/me/accounts', icon: 'wallet', description: 'Abre el listado de cuentas del cliente.' },
    { label: 'Notificaciones', route: '/dashboard/notifications', icon: 'bell', description: 'Revisa avisos recientes del tenant.' },
    { label: 'Resumen', route: '/dashboard/me/summary', icon: 'layout-dashboard', description: 'Regresa al resumen del cliente.' }
  ];
}
