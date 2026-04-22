import { inject, Injectable } from "@angular/core";
import {
  catchError,
  finalize,
  map,
  Observable,
  shareReplay,
  throwError,
} from "rxjs";
import { AuthService } from "../../../features/auth/services/auth.service";

@Injectable({
  providedIn: "root",
})
export class SessionRefreshCoordinatorService {
  private readonly authService = inject(AuthService);

  private refreshRequest$: Observable<void> | null = null;

  /**
   * Refreshes the user's session.
   * @returns An observable that emits when the session is refreshed.
   */
  refresh(): Observable<void> {
    if (this.refreshRequest$) {
      return this.refreshRequest$;
    }

    this.refreshRequest$ = this.authService.refresh().pipe(
      map(() => void 0),
      catchError((error: unknown) => {
        this.authService.logout();
        return throwError(() => error);
      }),
      finalize(() => {
        this.refreshRequest$ = null;
      }),
      shareReplay(1)
    );

    return this.refreshRequest$;
  }
}
