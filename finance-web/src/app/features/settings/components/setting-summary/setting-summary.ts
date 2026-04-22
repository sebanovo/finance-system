import { Component, input } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideBuilding2,
  lucideGlobe,
  lucideMail,
  lucidePhone,
} from "@ng-icons/lucide";
import { HlmCardImports } from "@shared/ui/card";
import { TenantSettings } from "../../models/setting.type";

@Component({
  selector: "app-setting-summary",
  imports: [NgIcon, HlmCardImports],
  providers: [
    provideIcons({
      lucideBuilding2,
      lucideGlobe,
      lucideMail,
      lucidePhone,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./setting-summary.html",
  styleUrl: "./setting-summary.css",
})
export class SettingSummary {
  readonly settings = input.required<TenantSettings>();
}
