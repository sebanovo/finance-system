export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort?: unknown;
    offset?: number;
    unpaged?: boolean;
    paged?: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort?: unknown;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

export interface AccountOwnerResponse {
  id: string;
  userId: string;
  userEmail: string;
  userFirstName: string;
  userLastName: string;
  userFullName: string;
  accountNumber: string;
  accountName: string;
  accountNameLabel: string;
  customAlias: string;
  displayName: string;
  accountType: string;
  currency: string;
  availableBalance: number;
  heldBalance: number;
  totalBalance: number;
  status: string;
  statusReason: string | null;
  active: boolean;
  primary: boolean;
  openedAt: string;
  closedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AccountBalanceResponse {
  accountId: string;
  accountNumber: string;
  accountName: string;
  accountNameLabel: string;
  customAlias: string;
  displayName: string;
  accountType: string;
  currency: string;
  availableBalance: number;
  heldBalance: number;
  totalBalance: number;
  status: string;
  active: boolean;
}

export interface CreateAccountRequest {
  userId: string;
  accountName: string;
  customAlias?: string;
  accountType: string;
  currency: string;
  primary?: boolean;
}

export interface UpdateAccountRequest {
  accountName?: string;
  customAlias?: string;
  primary?: boolean;
}
