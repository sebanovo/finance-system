import { Component } from '@angular/core';
import { ServiceModulePlaceholderComponent, ServiceModuleShortcut } from '../../features/service-payments/ui/service-module-placeholder.component';

@Component({
  selector: 'app-my-service-payments-page',
  standalone: true,
  imports: [ServiceModulePlaceholderComponent],
  template: `
    <app-service-module-placeholder
      eyebrow="Mis servicios"
      title="Historial de pagos"
      description="Consulta los comprobantes y movimientos de tus pagos de servicios en el tenant."
      backLabel="Volver a mi dashboard"
      backRoute="/dashboard/me/summary"
      footerNote="Comprobantes"
      [shortcuts]="shortcuts">
    </app-service-module-placeholder>
  `
})
export class MyServicePaymentsPageComponent {
  readonly shortcuts: ServiceModuleShortcut[] = [
    { label: 'Servicios afiliados', route: '/dashboard/me/service-enrollments', icon: 'clipboard-list', description: 'Revisa tus servicios vinculados.' },
    { label: 'Mis cuentas', route: '/dashboard/me/accounts', icon: 'wallet', description: 'Consulta las cuentas activas del cliente.' },
    { label: 'Dashboard', route: '/dashboard/me/summary', icon: 'layout-dashboard', description: 'Vuelve al resumen personal.' },
    { label: 'Notificaciones', route: '/dashboard/notifications', icon: 'bell', description: 'Mantente al tanto de la actividad reciente.' }
  ];
}
