import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import {
  AssignedSubscriptionDto,
  AssignSubscriptionRequest,
  AssignSubscriptionResponse,
} from "../model/subscription-assignment.type";

@Injectable({
  providedIn: "root",
})
export class AssignSubscriptionApi {
  private readonly http = inject(HttpClient);

  assignSubscription(
    payload: AssignSubscriptionRequest
  ): Observable<AssignedSubscriptionDto> {
    return this.http
      .post<
        AssignSubscriptionResponse<AssignedSubscriptionDto>
      >("/api/platform/subscriptions/assign", payload)
      .pipe(map(response => response.data));
  }
}
