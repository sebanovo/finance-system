import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { HlmToasterImports } from "@shared/ui/sonner";
import { SessionStore } from "./core/session/store/session.store";
import { HealthCheckComponent } from "./health.components";

@Component({
  selector: "app-root",
  imports: [RouterOutlet, HlmToasterImports, HealthCheckComponent],
  templateUrl: "./app.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  readonly sessionStore = inject(SessionStore);
  protected readonly title = signal("finance-web");
}
