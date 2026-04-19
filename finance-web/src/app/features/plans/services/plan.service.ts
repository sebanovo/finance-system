import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { CreatePlanRequest, PlanDto, PlanResponse } from "../models/plan.type";

@Injectable({
  providedIn: "root",
})
export class PlansApi {
  private readonly http = inject(HttpClient);

  getPlans(): Observable<PlanDto[]> {
    return this.http
      .get<PlanResponse<PlanDto[]>>("/api/platform/plans")
      .pipe(map(response => response.data));
  }

  createPlan(payload: CreatePlanRequest): Observable<PlanDto> {
    return this.http
      .post<PlanResponse<PlanDto>>("/api/platform/plans", payload)
      .pipe(map(response => response.data));
  }

  getPlanById(planId: string): Observable<PlanDto> {
    return this.http
      .get<PlanResponse<PlanDto>>(`/api/platform/plans/${planId}`)
      .pipe(map(response => response.data));
  }

  activatePlan(planId: string): Observable<PlanDto> {
    return this.http
      .patch<
        PlanResponse<PlanDto>
      >(`/api/platform/plans/${planId}/activate`, {})
      .pipe(map(response => response.data));
  }

  deactivatePlan(planId: string): Observable<PlanDto> {
    return this.http
      .patch<
        PlanResponse<PlanDto>
      >(`/api/platform/plans/${planId}/deactivate`, {})
      .pipe(map(response => response.data));
  }
}
