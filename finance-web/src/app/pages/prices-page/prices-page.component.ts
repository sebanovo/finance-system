import { Component } from '@angular/core';
import { PublicNavbarComponent } from '../../features/landing';
import { PublicFooterComponent } from '../../shared/ui/public-layout';
import { PricingPageWidgetComponent } from '../../widgets/pricing';

@Component({
  selector: 'app-prices-page',
  standalone: true,
  imports: [PublicNavbarComponent, PricingPageWidgetComponent, PublicFooterComponent],
  template: `
    <div class="min-h-screen bg-[#F7FBF3]">
      <app-public-navbar mode="prices" />
      <app-pricing-page-widget />
      <app-public-footer />
    </div>
  `
})
export class PricesPageComponent {}
