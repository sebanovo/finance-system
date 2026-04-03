export interface ProductListParams {
  search?: string;
  category?: string;
  status?: 'active' | 'inactive' | 'out_of_stock';
  page?: number;
  pageSize?: number;
}