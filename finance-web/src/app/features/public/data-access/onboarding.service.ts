import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../core/models/api-response.model';

/**Matches: platform.onboarding.application.dto.PublicSignupRequest*/
export type SignupRequest = {
  companyName: string;
  tenantSlug: string;
  adminEmail: string;
  password: string;
  firstName: string;
  lastName: string;
};

/**Matches: platform.onboarding.application.dto.PublicSignupResponse*/
export type SignupResponse = {
  tenantId: string;
  tenantSlug: string;
  companyName: string;
  adminEmail: string;
  initialRole: string;
  currentPlanCode: string;
  subscriptionStatus: string;
  trialExpiresAt: string;
  loginHint: string;
};

@Injectable({
  providedIn: 'root',
})
export class OnboardingService {
  private http = inject(HttpClient);
  private apiUrl = '/api/public';

  /**Registra una nueva empresa y su administrador
   * Consume: POST /api/public/signup*/
  signup(request: SignupRequest): Observable<SignupResponse> {
    return this.http
      .post<ApiResponse<SignupResponse>>(`${this.apiUrl}/signup`, request)
      .pipe(map((response) => response.data));
  }
}