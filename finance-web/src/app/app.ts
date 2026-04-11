import { Component, signal } from "@angular/core";
import { AppBootstrap } from "./core/app-bootstrap/app-bootstrap";
import { HealthCheckComponent } from "./health.components";

@Component({
  selector: "app-root",
  imports: [AppBootstrap, HealthCheckComponent],
  template: "<app-app-bootstrap />",
})
export class App {
  protected readonly title = signal("finance-web");
}
