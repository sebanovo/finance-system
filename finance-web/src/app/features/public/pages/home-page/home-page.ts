import { Component } from '@angular/core';
import { HomeContactSection } from './sections/home-contact-section/home-contact-section';
import { HomeHeroSection } from './sections/home-hero-section/home-hero-section';
import { HomeServicesSection } from './sections/home-services-section/home-services-section';

@Component({
  selector: 'app-home-page',
  imports: [HomeHeroSection, HomeServicesSection, HomeContactSection],
  templateUrl: './home-page.html',
})
export class HomePage { }
