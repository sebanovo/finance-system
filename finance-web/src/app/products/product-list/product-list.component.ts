import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProductService } from '../data-access/product.service';
import { Product } from '../data-access/product.model';
import { ProductListParams } from '../data-access/product.params';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent {
  private readonly productService = inject(ProductService);

  readonly products = signal<Product[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');

  readonly filters = signal<ProductListParams>({
    search: '',
    category: '',
    status: 'active',
    page: 1,
    pageSize: 10,
  });

  constructor() {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.productService.getProducts(this.filters()).subscribe({
      next: (products) => {
        this.products.set(products);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.errorMessage.set('Error loading products');
        this.isLoading.set(false);
      },
    });
  }

  getFinalPrice(product: Product): number {
    return product.price - (product.price * product.discountPercentage) / 100;
  }
}