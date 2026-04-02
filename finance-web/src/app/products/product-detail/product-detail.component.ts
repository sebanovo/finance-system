import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../data-access/product.service';
import { Product } from '../data-access/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css',
})
export class ProductDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly productService = inject(ProductService);

  readonly product = signal<Product | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.errorMessage.set('Invalid product id');
      return;
    }

    this.loadProduct(id);
  }

  loadProduct(id: number): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading product detail:', error);
        this.errorMessage.set('Error loading product detail');
        this.isLoading.set(false);
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }

  getFinalPrice(product: Product): number {
    return product.price - (product.price * product.discountPercentage) / 100;
  }
}