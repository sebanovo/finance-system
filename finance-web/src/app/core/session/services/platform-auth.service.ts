import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AccessTokenService } from '../../http/services/access-token.service';

@Injectable({ providedIn: 'root' })
export class PlatformAuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenService = inject(AccessTokenService);
  private readonly router = inject(Router);

  readonly user = signal<any | null>(null);

  login(credentials: any): Observable<any> {
    return this.http.post<any>('/api/auth/login', credentials).pipe(
      tap((response) => {
        this.tokenService.setTokens(response.data);
        this.getMe().subscribe();
      })
    );
  }

  getMe(): Observable<any> {
    return this.http.get<any>('/api/auth/me').pipe(
      tap(res => this.user.set(res.data))
    );
  }

  logout(): void {
    this.tokenService.clearTokens();
    this.user.set(null);
    this.router.navigate(['/platform/auth/login']);
  }
}
