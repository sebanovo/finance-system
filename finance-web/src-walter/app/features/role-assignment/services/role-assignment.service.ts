import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import {
  AssignUserRolesRequest,
  UserRoleAssignmentDto,
  UserRoleAssignmentResponse,
} from "../model/role-assignment.type";

@Injectable({
  providedIn: "root",
})
export class UserRoleAssignmentApi {
  private readonly http = inject(HttpClient);

  getUserRoles(userId: string): Observable<UserRoleAssignmentDto> {
    return this.http
      .get<
        UserRoleAssignmentResponse<UserRoleAssignmentDto>
      >(`/api/access/users/${userId}/roles`)
      .pipe(map(response => response.data));
  }

  assignUserRoles(
    userId: string,
    payload: AssignUserRolesRequest
  ): Observable<void> {
    return this.http.put<void>(`/api/access/users/${userId}/roles`, payload);
  }
}
