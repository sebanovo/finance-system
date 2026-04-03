export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  brand: string;
  category: string;
  price: number;
  discountPercentage: number;
  stock: number;
  status: 'active' | 'inactive' | 'out_of_stock';
  rating: number;
  tags: string[];
  thumbnailUrl: string;
  createdAt: string;
}