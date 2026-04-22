import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { PermissionDto, PermissionResponse } from "../model/permission.type";

@Injectable({
  providedIn: "root",
})
export class PermissionsApi {
  private readonly http = inject(HttpClient);

  getPermissions(): Observable<PermissionDto[]> {
    return this.http
      .get<PermissionResponse<PermissionDto[]>>("/api/access/permissions")
      .pipe(map(response => response.data));
  }
}
