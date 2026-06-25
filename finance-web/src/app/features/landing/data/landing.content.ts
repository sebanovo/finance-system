import {
  LandingFeatureCard,
  LandingFeatureItem,
  LandingMetricCard,
  LandingPlanPreviewCard,
  LandingVideoCard
} from '../model/landing-plan.model';
import { LandingAppDownloadData } from '../model/landing-app-download.model';

export const LANDING_YOUTUBE_CHANNEL_URL = 'https://youtube.com/@prospera-6777?si=Bb9wP5j311yJgtKF';

export const LANDING_HERO_CARDS: LandingFeatureItem[] = [
  {
    icon: 'users',
    title: 'Multitenant',
    description: 'Cada tenant con su propio contexto, usuarios y datos aislados.'
  },
  {
    icon: 'camera',
    title: 'Face-recognition',
    description: 'Activa el acceso con rostro y usa la foto del perfil.'
  },
  {
    icon: 'play',
    title: 'Tutoriales',
    description: 'Videos cortos para empezar en minutos, sin perder tiempo.'
  }
];

export const LANDING_FEATURES: LandingFeatureCard[] = [
  {
    icon: 'wallet',
    title: 'Cuentas y saldos',
    description: 'Administra cuentas, saldos disponibles, saldos retenidos y estados operativos desde un solo panel.'
  },
  {
    icon: 'arrow-right-left',
    title: 'Transacciones controladas',
    description: 'Procesa depósitos, retiros, pagos, transferencias y reversos con trazabilidad completa.'
  },
  {
    icon: 'shield-check',
    title: 'Roles, límites y seguridad',
    description: 'Controla permisos, límites operativos y restricciones por plan sin mezclar roles con suscripción.'
  },
  {
    icon: 'bar-chart-3',
    title: 'Dashboard y reportes',
    description: 'Visualiza métricas financieras, uso del sistema, actividad y reportes dinámicos para tomar decisiones.'
  }
];

export const LANDING_METRICS: LandingMetricCard[] = [
  { value: 'Multi-tenant', label: 'Arquitectura SaaS' },
  { value: 'Stripe', label: 'Suscripciones reales' },
  { value: 'RBAC', label: 'Roles y permisos' }
];

export const LANDING_VIDEOS: LandingVideoCard[] = [
  {
    title: 'Como iniciar sesión con face-recognition',
    description: 'Aprende cómo ingresar con rostro usando la foto de perfil y el flujo de acceso facial del sistema.',
    url: 'https://www.youtube.com/embed/RgVVV59qzMI?si=2qieICAG3MGf8-VC',
    badge: 'Acceso con rostro'
  },
  {
    title: 'Como pagar servicios',
    description: 'Mira cómo registrar y pagar servicios desde la experiencia del tenant, paso a paso.',
    url: 'https://www.youtube.com/embed/Q907PF4RQXU?si=FMCvZAtQ_IRFxTUC',
    badge: 'Pagos de servicios'
  }
];

export const LANDING_PLAN_PREVIEWS: LandingPlanPreviewCard[] = [
  {
    code: 'DEMO',
    name: 'Demo',
    description: 'Un entorno gratuito para conocer la plataforma y probar el flujo base.',
    priceLabel: 'Gratis',
    badge: 'Inicio rápido',
    ctaLabel: 'Crear tenant',
    ctaLink: '/onboarding'
  },
  {
    code: 'BASIC',
    name: 'Basic',
    description: 'Para equipos pequeños que necesitan operar con orden y menos fricción.',
    priceLabel: 'Desde USD 9.99',
    ctaLabel: 'Empezar',
    ctaLink: '/prices'
  },
  {
    code: 'PRO',
    name: 'Pro',
    description: 'Más capacidad, más control y una operación más completa para crecer.',
    priceLabel: 'Desde USD 19.99',
    badge: 'Recomendado',
    ctaLabel: 'Elegir Pro',
    ctaLink: '/prices'
  },
  {
    code: 'ENTERPRISE',
    name: 'Enterprise',
    description: 'Para despliegues que necesitan acompañamiento comercial y configuración a medida.',
    priceLabel: 'A medida',
    ctaLabel: 'Ver inicio',
    ctaLink: '/prices'
  }
];

export const LANDING_APP_DOWNLOAD: LandingAppDownloadData = {
  badge: 'Aplicación móvil',
  title: 'Finance Web también en tu móvil',
  description:
    'Descarga la app móvil desde Drive y gestiona cuentas, pagos de servicios, perfil y acceso seguro desde tu dispositivo.',
  driveDownloadUrl: 'https://drive.google.com',
  qrImageUrl: '/qr-app.png',
  features: [
    {
      icon: 'shield-check',
      title: 'Acceso seguro',
      description: 'Ingresa con tus credenciales y mantén tu sesión protegida.'
    },
    {
      icon: 'credit-card',
      title: 'Pagos rápidos',
      description: 'Consulta y paga servicios desde el móvil.'
    },
    {
      icon: 'bell',
      title: 'Notificaciones',
      description: 'Revisa avisos importantes de tu cuenta.'
    }
  ]
};
