import { http, HttpResponse } from 'msw';
import { productsMock } from '../data/products.mock';

export const productsHandlers = [
  http.get('/api/products', ({ request }) => {
    const url = new URL(request.url);

    const search = url.searchParams.get('search')?.toLowerCase() ?? '';
    const category = url.searchParams.get('category');
    const status = url.searchParams.get('status');

    let result = [...productsMock];

    if (search) {
      result = result.filter((product) =>
        product.name.toLowerCase().includes(search)
      );
    }

    if (category) {
      result = result.filter((product) => product.category === category);
    }

    if (status) {
      result = result.filter((product) => product.status === status);
    }

    return HttpResponse.json(result);
  }),

  http.get('/api/products/:id', ({ params }) => {
    const id = Number(params['id']);

    const product = productsMock.find((item) => item.id === id);

    if (!product) {
      return HttpResponse.json(
        { message: 'Product not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(product);
  }),
];