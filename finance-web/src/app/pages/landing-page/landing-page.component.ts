import { Component } from '@angular/core';
import { LandingPageComponent as PublicLandingPageComponent } from '../../features/landing/ui/landing-page/landing-page.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [PublicLandingPageComponent],
  template: `<app-public-landing-page />`
})
export class LandingPageComponent {}
