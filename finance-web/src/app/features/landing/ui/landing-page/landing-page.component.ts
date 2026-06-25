import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import {
  LANDING_FEATURES,
  LANDING_HERO_CARDS,
  LANDING_METRICS,
  LANDING_PLAN_PREVIEWS,
  LANDING_VIDEOS,
  LANDING_YOUTUBE_CHANNEL_URL
} from '../../data/landing.content';
import { LandingFeatureCard, LandingMetricCard, LandingPlanPreviewCard, LandingVideoCard } from '../../model/landing-plan.model';
import { LandingCtaComponent } from '../landing-cta/landing-cta.component';
import { LandingFeaturesComponent } from '../landing-features/landing-features.component';
import { LandingHeroComponent } from '../landing-hero/landing-hero.component';
import { LandingMetricsComponent } from '../landing-metrics/landing-metrics.component';
import { LandingPlanPreviewComponent } from '../landing-plan-preview/landing-plan-preview.component';
import { LandingQuickCardsComponent } from '../landing-quick-cards/landing-quick-cards.component';
import { LandingVideosComponent } from '../landing-videos/landing-videos.component';
import { PublicNavbarComponent } from '../public-navbar/public-navbar.component';

@Component({
  selector: 'app-public-landing-page',
  standalone: true,
  imports: [
    CommonModule,
    PublicNavbarComponent,
    LandingHeroComponent,
    LandingQuickCardsComponent,
    LandingMetricsComponent,
    LandingFeaturesComponent,
    LandingPlanPreviewComponent,
    LandingVideosComponent,
    LandingCtaComponent
  ],
  template: `
    <div class="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(46,125,50,0.14),_transparent_35%),radial-gradient(circle_at_top_right,_rgba(76,175,80,0.10),_transparent_30%),linear-gradient(180deg,_#ffffff_0%,_#f6fbf3_56%,_#eef7ea_100%)] text-[#12391a]">
      <app-public-navbar />

      <main class="mx-auto flex w-full max-w-7xl flex-col gap-14 px-4 pb-16 sm:px-6 lg:px-8">
        <app-landing-hero />
        <app-landing-quick-cards [cards]="heroCards" />
        <app-landing-metrics [metrics]="metrics" />
        <app-landing-features [features]="features" />
        <app-landing-plan-preview [plans]="plans" />
        <app-landing-videos [videos]="videos" [youtubeChannelUrl]="youtubeChannelUrl" />
        <app-landing-cta [youtubeChannelUrl]="youtubeChannelUrl" />
      </main>
    </div>
  `
})
export class LandingPageComponent {
  readonly youtubeChannelUrl = LANDING_YOUTUBE_CHANNEL_URL;
  readonly heroCards: LandingFeatureCard[] = LANDING_HERO_CARDS;
  readonly features: LandingFeatureCard[] = LANDING_FEATURES;
  readonly metrics: LandingMetricCard[] = LANDING_METRICS;
  readonly plans: LandingPlanPreviewCard[] = LANDING_PLAN_PREVIEWS;
  readonly videos: Array<LandingVideoCard & { safeUrl: SafeResourceUrl }>;

  constructor(private readonly sanitizer: DomSanitizer) {
    this.videos = LANDING_VIDEOS.map((video) => ({
      ...video,
      safeUrl: this.sanitizer.bypassSecurityTrustResourceUrl(video.url)
    }));
  }
}
