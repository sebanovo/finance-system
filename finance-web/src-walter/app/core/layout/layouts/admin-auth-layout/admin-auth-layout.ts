import { Component } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { remixCurrencyFill } from "@ng-icons/remixicon";
import { hexToOklchCss } from "../../../../shared/lib/color/hex-to-oklch";

@Component({
  selector: "app-admin-auth-layout",
  imports: [RouterOutlet, NgIcon, RouterLink],
  providers: [provideIcons({ remixCurrencyFill })],
  templateUrl: "./admin-auth-layout.html",
  styleUrl: "./admin-auth-layout.css",
})
export class AdminAuthLayout {
  selectedColor = "";
  selectedFont = "";

  onColorChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedColor = input.value;
    console.log("Color seleccionado:", this.selectedColor);

    const oklchColor = hexToOklchCss(this.selectedColor);
    if (oklchColor) {
      document.documentElement.style.setProperty("--background", oklchColor);

      console.log("HEX:", this.selectedColor);
      console.log("OKLCH:", oklchColor);
    }
  }

  onFontChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const font = select.value;
    this.selectedFont = font;

    document.documentElement.style.setProperty(
      "--font-sans",
      `${font}, system-ui, sans-serif`
    );
  }
}
