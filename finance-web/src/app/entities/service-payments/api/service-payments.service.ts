import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../shared/api';
import {
  CancelServiceBillRequest,
  ChangeServiceProviderStatusRequest,
  CreateBankServicePaymentRequest,
  CreateAssistedServicePaymentRequest,
  CreateMyServicePaymentRequest,
  CreateServiceBillRequest,
  CreateServiceCustomerRequest,
  CreateServiceEnrollmentRequest,
  CreateServiceProviderRequest,
  MyServiceEnrollmentFilter,
  MyServicePaymentFilter,
  PageResponse,
  PlatformServiceBillFilter,
  PlatformServiceBillPaymentFilter,
  PlatformServiceCustomerFilter,
  PlatformServiceProviderFilter,
  QueryServiceBillsRequest,
  QueryServiceBillsResponse,
  ServiceBillPaymentResponse,
  ServiceBillResponse,
  ServiceCustomerResponse,
  ServiceEnrollmentResponse,
  ServicePaymentResponse,
  ServiceProviderResponse,
  ServiceProviderCatalogResponse,
  TenantServicePaymentFilter,
  UpdateServiceCustomerRequest,
  UpdateServiceProviderRequest
} from '../model/service-payments.model';

@Injectable({
  providedIn: 'root'
})
export class ServicePaymentsService {
  private readonly http = inject(HttpClient);

  private readonly PLATFORM_URL = `${environment.apiUrl}/api/platform`;
  private readonly TENANT_URL = `${environment.apiUrl}/api`;
  private readonly MY_URL = `${environment.apiUrl}/api/me`;

  private buildParams<T extends object>(
    page = 0,
    size = 20,
    filters?: T
  ): HttpParams {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    Object.entries(filters ?? {}).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, String(value));
      }
    });

    return params;
  }

  // PLATFORM - SERVICE PROVIDERS
  createPlatformProvider(request: CreateServiceProviderRequest): Observable<ApiResponse<ServiceProviderResponse>> {
    return this.http.post<ApiResponse<ServiceProviderResponse>>(`${this.PLATFORM_URL}/service-providers`, request);
  }

  listPlatformProviders(
    page = 0,
    size = 20,
    filter: PlatformServiceProviderFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceProviderResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceProviderResponse>>>(
      `${this.PLATFORM_URL}/service-providers`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  getPlatformProvider(id: string): Observable<ApiResponse<ServiceProviderResponse>> {
    return this.http.get<ApiResponse<ServiceProviderResponse>>(`${this.PLATFORM_URL}/service-providers/${id}`);
  }

  updatePlatformProvider(id: string, request: UpdateServiceProviderRequest): Observable<ApiResponse<ServiceProviderResponse>> {
    return this.http.patch<ApiResponse<ServiceProviderResponse>>(`${this.PLATFORM_URL}/service-providers/${id}`, request);
  }

  changePlatformProviderStatus(
    id: string,
    request: ChangeServiceProviderStatusRequest
  ): Observable<ApiResponse<ServiceProviderResponse>> {
    return this.http.patch<ApiResponse<ServiceProviderResponse>>(`${this.PLATFORM_URL}/service-providers/${id}/status`, request);
  }

  // PLATFORM - SERVICE CUSTOMERS
  createPlatformServiceCustomer(request: CreateServiceCustomerRequest): Observable<ApiResponse<ServiceCustomerResponse>> {
    return this.http.post<ApiResponse<ServiceCustomerResponse>>(`${this.PLATFORM_URL}/service-customers`, request);
  }

  listPlatformServiceCustomers(
    page = 0,
    size = 20,
    filter: PlatformServiceCustomerFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceCustomerResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceCustomerResponse>>>(
      `${this.PLATFORM_URL}/service-customers`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  getPlatformServiceCustomer(id: string): Observable<ApiResponse<ServiceCustomerResponse>> {
    return this.http.get<ApiResponse<ServiceCustomerResponse>>(`${this.PLATFORM_URL}/service-customers/${id}`);
  }

  updatePlatformServiceCustomer(id: string, request: UpdateServiceCustomerRequest): Observable<ApiResponse<ServiceCustomerResponse>> {
    return this.http.patch<ApiResponse<ServiceCustomerResponse>>(`${this.PLATFORM_URL}/service-customers/${id}`, request);
  }

  // PLATFORM - SERVICE BILLS
  createPlatformServiceBill(request: CreateServiceBillRequest): Observable<ApiResponse<ServiceBillResponse>> {
    return this.http.post<ApiResponse<ServiceBillResponse>>(`${this.PLATFORM_URL}/service-bills`, request);
  }

  listPlatformServiceBills(
    page = 0,
    size = 20,
    filter: PlatformServiceBillFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceBillResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceBillResponse>>>(
      `${this.PLATFORM_URL}/service-bills`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  getPlatformServiceBill(id: string): Observable<ApiResponse<ServiceBillResponse>> {
    return this.http.get<ApiResponse<ServiceBillResponse>>(`${this.PLATFORM_URL}/service-bills/${id}`);
  }

  cancelPlatformServiceBill(id: string, request: CancelServiceBillRequest): Observable<ApiResponse<ServiceBillResponse>> {
    return this.http.patch<ApiResponse<ServiceBillResponse>>(`${this.PLATFORM_URL}/service-bills/${id}/cancel`, request);
  }

  // PLATFORM - GLOBAL SERVICE BILL PAYMENTS
  listPlatformServiceBillPayments(
    page = 0,
    size = 20,
    filter: PlatformServiceBillPaymentFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceBillPaymentResponse>>> {
    const { payerUserId, ...rest } = filter;
    return this.http.get<ApiResponse<PageResponse<ServiceBillPaymentResponse>>>(
      `${this.PLATFORM_URL}/service-bill-payments`,
      { params: this.buildParams(page, size, { ...rest, userId: payerUserId }) }
    );
  }

  getPlatformServiceBillPayment(id: string): Observable<ApiResponse<ServiceBillPaymentResponse>> {
    return this.http.get<ApiResponse<ServiceBillPaymentResponse>>(`${this.PLATFORM_URL}/service-bill-payments/${id}`);
  }

  // TENANT ADMIN - SERVICE PROVIDERS / BILLS / PAYMENTS
  listTenantServiceProviders(
    page = 0,
    size = 20,
    filter: PlatformServiceProviderFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceProviderResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceProviderResponse>>>(
      `${this.TENANT_URL}/service-providers`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  queryTenantServiceBills(request: QueryServiceBillsRequest): Observable<ApiResponse<QueryServiceBillsResponse>> {
    return this.http.post<ApiResponse<QueryServiceBillsResponse>>(`${this.TENANT_URL}/service-bills/query`, request);
  }

  createBankServicePayment(
    request: CreateBankServicePaymentRequest
  ): Observable<ApiResponse<ServicePaymentResponse>> {
    return this.http.post<ApiResponse<ServicePaymentResponse>>(`${this.TENANT_URL}/service-payments`, request);
  }

  createAssistedServicePayment(
    request: CreateAssistedServicePaymentRequest
  ): Observable<ApiResponse<ServicePaymentResponse>> {
    const { userId: _userId, ...bankRequest } = request;
    return this.createBankServicePayment(bankRequest);
  }

  listTenantServicePayments(
    page = 0,
    size = 20,
    filter: TenantServicePaymentFilter = {}
  ): Observable<ApiResponse<PageResponse<ServicePaymentResponse>>> {
    const { payerUserId, ...rest } = filter;
    return this.http.get<ApiResponse<PageResponse<ServicePaymentResponse>>>(
      `${this.TENANT_URL}/service-payments`,
      { params: this.buildParams(page, size, { ...rest, userId: payerUserId }) }
    );
  }

  getTenantServicePayment(id: string): Observable<ApiResponse<ServicePaymentResponse>> {
    return this.http.get<ApiResponse<ServicePaymentResponse>>(`${this.TENANT_URL}/service-payments/${id}`);
  }

  // CLIENT - MY SERVICE PROVIDERS / ENROLLMENTS / PAYMENTS
  listMyServiceProviders(
    page = 0,
    size = 20,
    filter: PlatformServiceProviderFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceProviderResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceProviderResponse>>>(
      `${this.MY_URL}/service-providers`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  listMyServiceProviderCatalog(): Observable<ApiResponse<ServiceProviderCatalogResponse[]>> {
    return this.http.get<ApiResponse<ServiceProviderCatalogResponse[]>>(
      `${this.MY_URL}/service-providers/catalog`
    );
  }

  listTenantServiceProviderCatalog(): Observable<ApiResponse<ServiceProviderCatalogResponse[]>> {
    return this.http.get<ApiResponse<ServiceProviderCatalogResponse[]>>(
      `${this.TENANT_URL}/service-providers/catalog`
    );
  }

  listMyServiceEnrollments(
    page = 0,
    size = 20,
    filter: MyServiceEnrollmentFilter = {}
  ): Observable<ApiResponse<PageResponse<ServiceEnrollmentResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServiceEnrollmentResponse>>>(
      `${this.MY_URL}/service-enrollments`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  createMyServiceEnrollment(request: CreateServiceEnrollmentRequest): Observable<ApiResponse<ServiceEnrollmentResponse>> {
    return this.http.post<ApiResponse<ServiceEnrollmentResponse>>(`${this.MY_URL}/service-enrollments`, request);
  }

  deleteMyServiceEnrollment(id: string): Observable<ApiResponse<ServiceEnrollmentResponse>> {
    return this.http.delete<ApiResponse<ServiceEnrollmentResponse>>(`${this.MY_URL}/service-enrollments/${id}`);
  }

  queryMyServiceBills(request: QueryServiceBillsRequest): Observable<ApiResponse<QueryServiceBillsResponse>> {
    return this.http.post<ApiResponse<QueryServiceBillsResponse>>(`${this.MY_URL}/service-bills/query`, request);
  }

  createMyServicePayment(request: CreateMyServicePaymentRequest): Observable<ApiResponse<ServicePaymentResponse>> {
    return this.http.post<ApiResponse<ServicePaymentResponse>>(`${this.MY_URL}/service-payments`, request);
  }

  listMyServicePayments(
    page = 0,
    size = 20,
    filter: MyServicePaymentFilter = {}
  ): Observable<ApiResponse<PageResponse<ServicePaymentResponse>>> {
    return this.http.get<ApiResponse<PageResponse<ServicePaymentResponse>>>(
      `${this.MY_URL}/service-payments`,
      { params: this.buildParams(page, size, filter) }
    );
  }

  getMyServicePayment(id: string): Observable<ApiResponse<ServicePaymentResponse>> {
    return this.http.get<ApiResponse<ServicePaymentResponse>>(`${this.MY_URL}/service-payments/${id}`);
  }
}
