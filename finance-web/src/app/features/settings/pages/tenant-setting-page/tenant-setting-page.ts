import { Component, computed, inject, OnInit } from "@angular/core";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideBuilding2,
  lucideSettings2,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { SettingForm } from "../../components/setting-form/setting-form";
import { SettingSummary } from "../../components/setting-summary/setting-summary";
import { TenantSettingsFormValue } from "../../models/setting.type";
import { TenantSettingsStore } from "../../store/setting.store";

@Component({
  selector: "app-tenant-setting-page",
  imports: [
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmCardImports,
    HlmSkeletonImports,
    SettingForm,
    SettingSummary,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideBuilding2,
      lucideSettings2,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./tenant-setting-page.html",
  styleUrl: "./tenant-setting-page.css",
})
export class TenantSettingPage implements OnInit {
  readonly store = inject(TenantSettingsStore);

  ngOnInit(): void {
    void this.store.loadTenantSettings();
  }

  async onSubmit(formValue: TenantSettingsFormValue): Promise<void> {
    this.store.clearUpdateError();

    const updatedSettings = await this.store.updateTenantSettings(formValue);

    if (!updatedSettings) {
      toast("No se pudo actualizar la configuración", {
        description:
          this.store.updateError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast("Configuración actualizada correctamente", {
      description: "Los cambios del tenant fueron guardados con éxito.",
    });
  }

  onRetry(): void {
    void this.store.reloadTenantSettings();
  }

  readonly initialFormValue = computed<TenantSettingsFormValue | null>(() => {
    const settings = this.store.settings();

    if (!settings) {
      return null;
    }

    return {
      companyName: settings.companyName,
      legalName: settings.legalName,
      timezone: settings.timezone,
      currency: settings.currency,
      contactEmail: settings.contactEmail,
      contactPhone: settings.contactPhone,
    };
  });
}
