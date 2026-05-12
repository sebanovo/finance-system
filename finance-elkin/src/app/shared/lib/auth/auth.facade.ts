import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthStorageService } from '../storage/auth-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  private readonly authStorage = inject(AuthStorageService);
  private readonly router = inject(Router);

  /**
   * Cierra la sesión del usuario actual limpiando el almacenamiento
   * y redirigiendo a la vista de login.
   */
  logout(): void {
    // Limpiar almacenamiento persistente
    this.authStorage.clear();
    
    // Aquí se podría invalidar caché, limpiar estados globales adicionales, etc.
    
    // Redirigir al inicio de sesión
    this.router.navigate(['/login']);
  }
}
