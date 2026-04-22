import { Component, inject, OnInit } from '@angular/core';
import { HealthService } from './health.service';

@Component({
  selector: 'app-health-check',
  standalone: true,
  template: `
    <div class="card">
      <h3>Estado del Sistema</h3>
      <p>Estado actual: <strong>{{ health() }}</strong></p>
      <button (click)="refresh()">Actualizar</button>
    </div>
  `
})
export class HealthCheckComponent implements OnInit {
  private healthService = inject(HealthService);
  
  // Exponemos el signal al template
  health = this.healthService.healthStatus;

  ngOnInit() {
    this.healthService.checkHealth();
  }

  refresh() {
    this.healthService.checkHealth();
  }
}