import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";

@Injectable({ providedIn: "root" })
export class TenantService {
  private api = "https://jsonplaceholder.typicode.com/users";

  private http = inject(HttpClient);

  getTenants() {
    return this.http.get<any[]>(this.api);
  }

  getUser() {
    return this.http.get<any[]>("/api/users");
  }
}
