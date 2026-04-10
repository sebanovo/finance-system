import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { Router } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideGalleryVerticalEnd } from "@ng-icons/lucide";
import {
  remixAppleFill,
  remixGithubFill,
  remixGoogleFill,
  remixGoogleLine,
} from "@ng-icons/remixicon";
import { HlmButtonImports } from "@shared/ui/button";
import {
  HlmField,
  HlmFieldImports,
  HlmFieldLabel,
  HlmFieldSeparator,
} from "@shared/ui/field";
import { HlmIconImports } from "@shared/ui/icon";
import { HlmInput } from "@shared/ui/input";

@Component({
  selector: "app-login-admin-page",
  imports: [
    HlmButtonImports,
    HlmIconImports,
    HlmFieldImports,
    HlmField,
    HlmFieldSeparator,
    HlmFieldLabel,
    HlmInput,
    NgIcon,
  ],
  providers: [
    provideIcons({
      remixAppleFill,
      remixGithubFill,
      remixGoogleFill,
      remixGoogleLine,
      lucideGalleryVerticalEnd,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./login-admin-page.html",
  styleUrl: "./login-admin-page.css",
})
export class LoginAdminPage {
  private readonly router = inject(Router);

  async onIngresarClick(): Promise<void> {
    await this.router.navigateByUrl("/app");
  }
}
