import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import {
  SubscriptionDto,
  SubscriptionResponse,
} from "../models/subscription.type";

@Injectable({
  providedIn: "root",
})
export class SubscriptionsApi {
  private readonly http = inject(HttpClient);

  getSubscriptions(): Observable<SubscriptionDto[]> {
    return this.http
      .get<
        SubscriptionResponse<SubscriptionDto[]>
      >("/api/platform/subscriptions")
      .pipe(map(response => response.data));
  }

  getSubscriptionById(subscriptionId: string): Observable<SubscriptionDto> {
    return this.http
      .get<
        SubscriptionResponse<SubscriptionDto>
      >(`/api/platform/subscriptions/${subscriptionId}`)
      .pipe(map(response => response.data));
  }

  getCurrentSubscription(): Observable<SubscriptionDto> {
    return this.http
      .get<SubscriptionResponse<SubscriptionDto>>("/api/subscription/current")
      .pipe(map(response => response.data));
  }
}
