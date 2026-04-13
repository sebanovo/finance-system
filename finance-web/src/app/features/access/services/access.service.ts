import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { PermissionDto } from "../models/access.dto";
import { AccessResponse } from "../models/access.response";

@Injectable({
  providedIn: "root",
})
export class AccessApi {
  private readonly http = inject(HttpClient);

  getPermissions(): Observable<PermissionDto[]> {
    return this.http
      .get<AccessResponse<PermissionDto[]>>("/api/access/permissions")
      .pipe(map(response => response.data));
  }
}
