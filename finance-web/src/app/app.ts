import { Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { SessionStore } from "./core/session/store/session.store";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: "./app.html",
})
export class App {
  protected readonly sessionStore = inject(SessionStore);
}
