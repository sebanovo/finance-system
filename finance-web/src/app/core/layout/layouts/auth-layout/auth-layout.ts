import { Component } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideGalleryVerticalEnd } from "@ng-icons/lucide";
import { remixCurrencyFill } from "@ng-icons/remixicon";

@Component({
  selector: "app-auth-layout",
  standalone: true,
  imports: [RouterOutlet, NgIcon, RouterLink],
  providers: [provideIcons({ lucideGalleryVerticalEnd, remixCurrencyFill })],
  templateUrl: "./auth-layout.html",
  styleUrl: "./auth-layout.css",
})
export class AuthLayout {}
