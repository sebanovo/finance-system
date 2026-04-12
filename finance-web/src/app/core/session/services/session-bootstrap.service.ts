import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { AuthUser } from "../model/auth-user.type";
import { SessionService } from "./session.service";

@Injectable({
  providedIn: "root",
})
export class SessionBootstrapService {
  private readonly sessionService = inject(SessionService);

  /**
   * Bootstraps the current session on application startup.
   */
  run(): Observable<AuthUser | null> {
    return this.sessionService.bootstrap();
  }
}
