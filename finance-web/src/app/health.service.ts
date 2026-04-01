import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HealthService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/health';

  // Signal para almacenar el estado de salud
  healthStatus = signal<string>('Cargando...');

  async checkHealth() {
    try {
      // Especificamos que esperamos texto, no JSON
      const response = await lastValueFrom(
        this.http.get(this.url, { responseType: 'text' })
      );
      this.healthStatus.set(response);
    } catch (error) {
      console.error('Error al conectar con el servidor', error);
      this.healthStatus.set('Servidor no disponible');
    }
  }
}