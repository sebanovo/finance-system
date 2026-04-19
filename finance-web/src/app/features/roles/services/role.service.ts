import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import {
  CreateRoleRequest,
  RoleDto,
  RoleResponse,
  UpdateRoleRequest,
} from "../model/role.type";

@Injectable({
  providedIn: "root",
})
export class RolesApi {
  private readonly http = inject(HttpClient);

  getRoles(): Observable<RoleDto[]> {
    return this.http
      .get<RoleResponse<RoleDto[]>>("/api/access/roles")
      .pipe(map(response => response.data));
  }

  createRole(payload: CreateRoleRequest): Observable<RoleDto> {
    return this.http
      .post<RoleResponse<RoleDto>>("/api/access/roles", payload)
      .pipe(map(response => response.data));
  }

  getRoleById(roleId: string): Observable<RoleDto> {
    return this.http
      .get<RoleResponse<RoleDto>>(`/api/access/roles/${roleId}`)
      .pipe(map(response => response.data));
  }

  updateRole(roleId: string, payload: UpdateRoleRequest): Observable<RoleDto> {
    return this.http
      .put<RoleResponse<RoleDto>>(`/api/access/roles/${roleId}`, payload)
      .pipe(map(response => response.data));
  }

  activateRole(roleId: string): Observable<RoleDto> {
    return this.http
      .patch<RoleResponse<RoleDto>>(`/api/access/roles/${roleId}/activate`, {})
      .pipe(map(response => response.data));
  }

  deactivateRole(roleId: string): Observable<RoleDto> {
    return this.http
      .patch<
        RoleResponse<RoleDto>
      >(`/api/access/roles/${roleId}/deactivate`, {})
      .pipe(map(response => response.data));
  }
}
