import { Component } from '@angular/core';
import { ReportingConsoleComponent } from '../../features/reporting/ui/reporting-console.component';

/** Platform (SuperAdmin) reporting console (new module). */
@Component({
  selector: 'app-platform-reporting-page',
  standalone: true,
  imports: [ReportingConsoleComponent],
  template: `<app-reporting-console scope="platform"></app-reporting-console>`
})
export class PlatformReportingPageComponent {}
