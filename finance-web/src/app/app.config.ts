import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authTokenInterceptor } from './shared/api';

import {
  LucideAngularModule, Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
  LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard,
  ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert,
  BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X, Bell,
  Plus, UserCircle2, Save, MoreHorizontal, Wallet, Pencil, CheckCircle,
  Play, Ban, Snowflake, XCircle, ArrowDownToLine, ArrowUpFromLine, Send,
  RotateCcw, Reply, ClipboardList, AlertCircle, AlertTriangle, BadgeCheck,
  Building, Clock, Info, PauseCircle, PlayCircle, Sparkles, Wand2,
  BarChart3, FileChartColumn,
  Database,
  Search,
  Archive,
  Camera,
  Trash2,
  ShieldCheck,
  LoaderCircle,
  TriangleAlert,
  CircleAlert,
  Upload
} from 'lucide-angular';


import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authTokenInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(LucideAngularModule.pick({
      Building2, User, ArrowRight, ArrowLeft, Eye, EyeOff,
      LayoutDashboard, Settings, Shield, Users, Key, Briefcase, CreditCard,
      ArrowRightLeft, DollarSign, RefreshCcw, Percent, Lock, ShieldAlert,
      BookOpen, Calendar, FileText, LogOut, ChevronDown, Menu, X, Bell,
      Plus, UserCircle2, Save, MoreHorizontal, Wallet, Pencil, CheckCircle,
      Play, Ban, Snowflake, XCircle, ArrowDownToLine, ArrowUpFromLine, Send,
      RotateCcw, Reply, ClipboardList, AlertCircle, AlertTriangle, BadgeCheck,
      Building, Clock, Info, PauseCircle, PlayCircle, Sparkles, Wand2,
      BarChart3, FileChartColumn, Database, Search, Archive,
      Camera, Trash2, ShieldCheck, LoaderCircle, TriangleAlert,
      CircleAlert, Upload
    }))

  ]
};
