import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-customer-services-shell',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet />`
})
export class CustomerServicesShellComponent {}
