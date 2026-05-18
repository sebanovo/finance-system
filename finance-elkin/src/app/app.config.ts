import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './shared/api';

import { 
  LucideAngularModule, Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
  LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard, 
  ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert, 
  BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X 
} from 'lucide-angular';


import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(LucideAngularModule.pick({ 
      Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
      LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard, 
      ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert, 
      BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X
    }))

  ]
};