import { Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { SessionBootstrapService } from "../session/services/session-bootstrap.service";
import { SessionStore } from "../session/store/session.store";

@Component({
  selector: "app-app-bootstrap",
  imports: [RouterOutlet],
  templateUrl: "./app-bootstrap.html",
  styleUrl: "./app-bootstrap.css",
})
export class AppBootstrap {
  private readonly sessionBootstrapService = inject(SessionBootstrapService);
  readonly sessionStore = inject(SessionStore);

  constructor() {
    this.sessionBootstrapService.run().subscribe();
  }
}
