import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PlatformStorageService } from '../../../features/platform/lib/platform-storage.service';

@Injectable({ providedIn: 'root' })
export class platformAuthGuard {
  private router = inject(Router);
  private platformStorage = inject(PlatformStorageService);

  canActivate(): boolean {
    const token = this.platformStorage.getToken();
    if (token) {
      return true;
    }
    this.router.navigate(['/platform/login']);
    return false;
  }
}