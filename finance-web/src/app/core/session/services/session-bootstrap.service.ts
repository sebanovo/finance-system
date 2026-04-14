import { inject, Injectable } from "@angular/core";
import { SessionStore } from "../store/session.store";

@Injectable({
  providedIn: "root",
})
export class SessionBootstrapService {
  private readonly sessionStore = inject(SessionStore);

  /**
   * Runs the session bootstrap process.
   * @returns A promise that resolves when the bootstrap process is complete.
   */
  runBootstrap(): Promise<void> {
    return this.sessionStore.bootstrap();
  }
}
