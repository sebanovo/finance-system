import { inject, Injectable } from "@angular/core";
import {
  catchError,
  finalize,
  map,
  Observable,
  shareReplay,
  switchMap,
  throwError,
} from "rxjs";

import { SessionApi } from "../api/session.api";
import { SessionService } from "./session.service";

@Injectable({
  providedIn: "root",
})
export class SessionRefreshCoordinatorService {
  private readonly sessionApi = inject(SessionApi);
  private readonly sessionService = inject(SessionService);

  /**
   * Holds the current refresh request while it is in flight.
   */
  private refreshRequest$: Observable<void> | null = null;

  /**
   * Executes the refresh flow and reuses the same request if already running.
   */
  refresh(): Observable<void> {
    if (this.refreshRequest$) {
      return this.refreshRequest$;
    }

    this.refreshRequest$ = this.sessionApi.refresh().pipe(
      map(() => void 0),
      shareReplay(1),
      catchError((error: unknown) => {
        return this.sessionService
          .terminateSession()
          .pipe(switchMap(() => throwError(() => error)));
      }),
      finalize(() => {
        this.refreshRequest$ = null;
      })
    );

    return this.refreshRequest$;
  }
}
