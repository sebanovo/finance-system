export type PublicNavigationType = 'section' | 'page';

export type PublicNavigationItem = {
  label: string;
  type: PublicNavigationType;
  fragment?: string;
  route?: string;
};

export const PUBLIC_NAVIGATION: PublicNavigationItem[] = [
  {
    label: 'Inicio',
    type: 'section',
    fragment: 'hero',
  },
  {
    label: 'Servicios',
    type: 'section',
    fragment: 'services',
  },
  {
    label: 'Precios',
    type: 'page',
    route: '/pricing',
  },
  {
    label: 'Contacto',
    type: 'section',
    fragment: 'contact',
  },
];
