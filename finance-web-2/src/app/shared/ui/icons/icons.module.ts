// shared/ui/icons/icons.module.ts
import { NgModule } from '@angular/core';
import { LucideAngularModule, 
  LayoutDashboard, Settings, Shield, Users, Key, Briefcase, 
  CreditCard, ArrowRightLeft, DollarSign, RefreshCcw, Percent, 
  Lock, ShieldAlert, BookOpen, Calendar, FileText, LogOut,
  Building, Receipt, Plus, X, Eye, PlayCircle, PauseCircle,
  EyeOff, Wand2, 
  ClipboardList  
} from 'lucide-angular';

@NgModule({
  imports: [
    LucideAngularModule.pick({
      LayoutDashboard, Settings, Shield, Users, Key, Briefcase,
      CreditCard, ArrowRightLeft, DollarSign, RefreshCcw, Percent,
      Lock, ShieldAlert, BookOpen, Calendar, FileText, LogOut,
      Building, Receipt, Plus, X, Eye, PlayCircle, PauseCircle,
      EyeOff, Wand2
    })
  ],
  exports: [LucideAngularModule]
})
export class IconsModule {}