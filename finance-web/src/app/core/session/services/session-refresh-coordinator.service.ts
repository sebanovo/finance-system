import { inject, Injectable } from "@angular/core";
import {
  catchError,
  finalize,
  firstValueFrom,
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
   * Refreshes the current authenticated session.
   */
  refresh(): Observable<void> {
    if (this.refreshRequest$) {
      return this.refreshRequest$;
    }

    this.refreshRequest$ = this.authService.refresh().pipe(
      map(() => void 0),
      catchError((error: unknown) => {
        void firstValueFrom(this.authService.logout()).catch(() => undefined);
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
