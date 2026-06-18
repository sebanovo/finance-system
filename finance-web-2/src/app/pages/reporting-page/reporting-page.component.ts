import { Component } from '@angular/core';
import { ReportingConsoleComponent } from '../../features/reporting/ui/reporting-console.component';

/** Tenant reporting console (new module). */
@Component({
  selector: 'app-reporting-page',
  standalone: true,
  imports: [ReportingConsoleComponent],
  template: `<app-reporting-console scope="tenant"></app-reporting-console>`
})
export class ReportingPageComponent {}
