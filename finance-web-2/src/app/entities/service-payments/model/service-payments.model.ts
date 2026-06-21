export type { PageResponse } from '../../access';

export type ServiceProviderCategory =
  | 'ELECTRICITY'
  | 'WATER'
  | 'INTERNET'
  | 'TV_CABLE'
  | string;

export type ServiceProviderStatus = 'ACTIVE' | 'INACTIVE' | string;
export type ServiceCustomerStatus = 'ACTIVE' | 'INACTIVE' | string;
export type ServiceBillStatus = 'PENDING' | 'PAID' | 'EXPIRED' | 'CANCELLED' | 'REVERSED' | string;
export type ServiceBillPaymentStatus = 'PAID' | 'REVERSED' | string;

export interface ServiceProviderSummaryResponse {
  id: string;
  code: string;
  name: string;
  category: ServiceProviderCategory;
}

export interface ServiceProviderResponse {
  id: string;
  code: string;
  name: string;
  category: ServiceProviderCategory;
  serviceCustomerCodeLabel: string | null;
  status: ServiceProviderStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateServiceProviderRequest {
  code: string;
  name: string;
  category: ServiceProviderCategory;
  serviceCustomerCodeLabel?: string | null;
}

export interface UpdateServiceProviderRequest {
  name?: string;
  category?: ServiceProviderCategory;
  serviceCustomerCodeLabel?: string | null;
}

export interface ChangeServiceProviderStatusRequest {
  status: ServiceProviderStatus;
}

export interface ServiceCustomerResponse {
  id: string;
  providerId: string;
  providerCode: string;
  providerName: string;
  providerCategory: ServiceProviderCategory;
  serviceCustomerCode: string;
  customerName: string;
  status: ServiceCustomerStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateServiceCustomerRequest {
  providerId: string;
  serviceCustomerCode: string;
  customerName: string;
}

export interface UpdateServiceCustomerRequest {
  customerName?: string;
  status?: ServiceCustomerStatus;
}

export interface ServiceBillResponse {
  id: string;
  providerId: string;
  providerCode: string;
  providerName: string;
  providerCategory: ServiceProviderCategory;
  serviceCustomerId: string;
  serviceCustomerCode: string;
  customerName: string;
  billingPeriod: string;
  amount: number;
  currency: string;
  dueDate: string;
  status: ServiceBillStatus;
  paidByTenantId: string | null;
  paidByTenantSlug: string | null;
  paidByUserId: string | null;
  paidByAccountId: string | null;
  paidByAccountNumber: string | null;
  paidTransactionId: string | null;
  paidAt: string | null;
  createdBySuperadminId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateServiceBillRequest {
  providerId: string;
  serviceCustomerCode: string;
  billingPeriod: string;
  amount: number;
  currency: string;
  dueDate: string;
}

export interface CancelServiceBillRequest {
  reason?: string | null;
}

export interface ServiceBillPaymentResponse {
  id: string;
  billId: string;
  providerId: string;
  providerCode: string;
  providerName: string;
  providerCategory: ServiceProviderCategory;
  serviceCustomerCode: string;
  serviceCustomerName: string;
  billingPeriod: string;
  paidByTenantId: string | null;
  paidByTenantSlug: string | null;
  paidByUserId: string | null;
  paidByAccountId: string | null;
  paidByAccountNumber: string | null;
  paidTransactionId: string | null;
  amount: number;
  currency: string;
  receiptNumber: string;
  idempotencyKey: string | null;
  status: ServiceBillPaymentStatus;
  paidAt: string;
  createdAt: string;
}

export interface ServiceEnrollmentResponse {
  id: string;
  provider: ServiceProviderSummaryResponse;
  serviceCustomerCode: string;
  serviceCustomerName: string | null;
  alias: string | null;
  status: 'ACTIVE' | 'INACTIVE' | string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateServiceEnrollmentRequest {
  providerId: string;
  serviceCustomerCode: string;
  alias?: string | null;
}

export interface ServiceBillQueryItemResponse {
  billId: string;
  billingPeriod: string;
  amount: number;
  currency: string;
  dueDate: string;
  status: ServiceBillStatus;
}

export interface QueryServiceBillsResponse {
  provider: ServiceProviderSummaryResponse;
  serviceCustomerCode: string;
  serviceCustomerName: string;
  bills: ServiceBillQueryItemResponse[];
}

export interface QueryServiceBillsRequest {
  providerId?: string;
  serviceCustomerCode?: string;
  enrollmentId?: string;
}

export interface CreateMyServicePaymentRequest {
  sourceAccountNumber: string;
  providerId: string;
  serviceCustomerCode: string;
  billId: string;
  enrollmentId?: string | null;
  idempotencyKey: string;
}

export interface CreateBankServicePaymentRequest {
  sourceAccountNumber: string;
  providerId: string;
  serviceCustomerCode: string;
  billId: string;
  idempotencyKey: string;
}

/**
 * Legacy payload kept for compatibility with older callers.
 * The active bank flow no longer requires userId.
 */
export interface CreateAssistedServicePaymentRequest {
  userId: string;
  sourceAccountNumber: string;
  providerId: string;
  serviceCustomerCode: string;
  billId: string;
  idempotencyKey: string;
}

export interface ServicePaymentResponse {
  paymentId: string;
  billId: string;
  transactionId: string;
  receiptNumber: string;
  provider: ServiceProviderSummaryResponse;
  serviceCustomerCode: string;
  serviceCustomerName: string;
  billingPeriod: string;
  amount: number;
  currency: string;
  sourceAccountNumber: string;
  status: ServiceBillPaymentStatus;
  paidAt: string;
}

export interface PlatformServiceProviderFilter {
  category?: string;
  status?: string;
  search?: string;
}

export interface PlatformServiceCustomerFilter {
  providerId?: string;
  status?: string;
  serviceCustomerCode?: string;
  search?: string;
}

export interface PlatformServiceBillFilter {
  providerId?: string;
  serviceCustomerCode?: string;
  status?: string;
  billingPeriod?: string;
  dueDateFrom?: string;
  dueDateTo?: string;
  paidByTenantSlug?: string;
}

export interface PlatformServiceBillPaymentFilter {
  providerId?: string;
  tenantSlug?: string;
  payerUserId?: string;
  accountNumber?: string;
  billId?: string;
  receiptNumber?: string;
  paidAtFrom?: string;
  paidAtTo?: string;
}

export interface MyServiceEnrollmentFilter {
  providerId?: string;
  category?: string;
  status?: string;
  search?: string;
}

export interface MyServicePaymentFilter {
  providerId?: string;
  receiptNumber?: string;
  billId?: string;
  paidAtFrom?: string;
  paidAtTo?: string;
}

export interface TenantServicePaymentFilter {
  providerId?: string;
  payerUserId?: string;
  accountNumber?: string;
  billId?: string;
  receiptNumber?: string;
  paidAtFrom?: string;
  paidAtTo?: string;
}
