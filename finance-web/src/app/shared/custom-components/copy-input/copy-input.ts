import { Component, input, signal } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideCheck, lucideCopy } from "@ng-icons/lucide";
import { HlmIconImports } from "@shared/ui/icon";
import { HlmInputGroupImports } from "@shared/ui/input-group";

@Component({
  selector: "app-copy-input",
  imports: [HlmInputGroupImports, HlmIconImports, NgIcon],
  providers: [provideIcons({ lucideCopy, lucideCheck })],
  templateUrl: "./copy-input.html",
  styleUrl: "./copy-input.css",
})
export class CopyInput {
  readonly value = input.required<string>();
  readonly placeholder = input<string>("");
  readonly ariaLabel = input<string>("Copiar");
  readonly buttonTitle = input<string>("Copiar");

  protected readonly isCopied = signal(false);

  protected copy(): void {
    const text = this.value();

    if (!text) {
      return;
    }

    void navigator.clipboard.writeText(text);
    this.isCopied.set(true);

    setTimeout(() => {
      this.isCopied.set(false);
    }, 3000);
  }
}
