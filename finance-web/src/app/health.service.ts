import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HealthService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/health';

  readonly healthStatus = signal<string>('Cargando...');

  async checkHealth(): Promise<void> {
    try {
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