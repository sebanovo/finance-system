import { Component } from '@angular/core';
import { LandingPageComponent as LandingFeature } from '../../features/landing/ui/landing-page/landing-page.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [LandingFeature],
  template: `<app-landing-page></app-landing-page>`
})
export class LandingPageComponent {}