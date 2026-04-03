import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from './product.model';
import { ProductListParams } from './product.params';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/products';

  getProducts(params?: ProductListParams): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl, {
      params: this.buildHttpParams(params),
    });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  private buildHttpParams(params?: ProductListParams): HttpParams {
    if (!params) {
      return new HttpParams();
    }

    const config: Record<string, string | number | undefined> = {
      search: params.search,
      category: params.category,
      status: params.status,
      page: params.page,
      pageSize: params.pageSize,
    };

    let httpParams = new HttpParams();

    Object.entries(config).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return httpParams;
  }
}