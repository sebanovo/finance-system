import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { hexToOklchCss } from "../../../../shared/lib/color/hex-to-oklch";

@Component({
  selector: "app-admin-auth-layout",
  imports: [RouterOutlet],
  templateUrl: "./admin-auth-layout.html",
  styleUrl: "./admin-auth-layout.css",
})
export class AdminAuthLayout {
  selectedColor = "";

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

    document.documentElement.style.setProperty(
      "--font-sans",
      `${font}, system-ui, sans-serif`
    );
  }
}
