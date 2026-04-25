import { Component, effect, inject, input, output } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import {
  lucideAlertCircle,
  lucideBadgeDollarSign,
  lucideCheck,
  lucideLoaderCircle,
  lucidePackagePlus,
} from "@ng-icons/lucide";
import { HlmAlertImports } from "@shared/ui/alert";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmEmptyImports } from "@shared/ui/empty";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { HlmSheetImports } from "@shared/ui/sheet";
import { HlmSkeletonImports } from "@shared/ui/skeleton";
import { toast } from "@spartan-ng/brain/sonner";
import { EmptyState } from "../../../../shared/custom-components/empty-state/empty-state";
import {
  AssignSubscriptionFormValue,
  AssignSubscriptionTenantContext,
} from "../../model/subscription-assignment.type";
import { AssignSubscriptionStore } from "../../store/subscription-assignment.store";

@Component({
  selector: "app-subscription-manage-drawer",
  imports: [
    ReactiveFormsModule,
    NgIcon,
    HlmAlertImports,
    HlmButtonImports,
    HlmEmptyImports,
    HlmFieldImports,
    HlmInputImports,
    HlmSheetImports,
    HlmSkeletonImports,
    EmptyState,
  ],
  providers: [
    provideIcons({
      lucideAlertCircle,
      lucideBadgeDollarSign,
      lucideCheck,
      lucideLoaderCircle,
      lucidePackagePlus,
    }),
  ],
  host: {
    style: "display: block",
  },
  templateUrl: "./subscription-manage-drawer.html",
  styleUrl: "./subscription-manage-drawer.css",
})
export class SubscriptionManageDrawer {
  readonly store = inject(AssignSubscriptionStore);
  private readonly fb = inject(FormBuilder);

  readonly open = input.required<boolean>();
  readonly tenant = input<AssignSubscriptionTenantContext | null>(null);

  readonly openChange = output<boolean>();
  readonly saved = output<void>();

  readonly form = this.fb.nonNullable.group({
    planCode: ["", [Validators.required]],
    overrideTrialDays: [null as number | null],
  });

  constructor() {
    effect(() => {
      const isOpen = this.open();
      const tenant = this.tenant();

      if (!isOpen || !tenant) {
        return;
      }

      this.form.reset(
        {
          planCode: "",
          overrideTrialDays: null,
        },
        { emitEvent: false }
      );

      void this.loadDrawerData(tenant.id);
    });
  }

  private async loadDrawerData(tenantId: string): Promise<void> {
    await this.store.loadForAssign(tenantId);

    const selectedPlanCode = this.store.selectedPlanCode();

    this.form.patchValue(
      {
        planCode: selectedPlanCode,
      },
      { emitEvent: false }
    );
  }

  // constructor() {
  //   effect(() => {
  //     const isOpen = this.open();
  //     const tenant = this.tenant();

  //     if (!isOpen || !tenant) {
  //       return;
  //     }

  //     this.form.reset(
  //       {
  //         planCode: "",
  //         overrideTrialDays: null,
  //       },
  //       { emitEvent: false }
  //     );

  //     void this.store.loadForAssign();
  //   });
  // }

  onOpenChange(isOpen: boolean): void {
    this.openChange.emit(isOpen);

    if (!isOpen) {
      this.store.resetState();
      this.form.reset();
    }
  }

  onClosed(): void {
    this.openChange.emit(false);
    this.store.resetState();
    this.form.reset();
  }

  onSelectPlan(planCode: string): void {
    this.store.selectPlan(planCode);
    this.form.controls.planCode.setValue(planCode);
    this.form.controls.planCode.markAsDirty();
  }

  isPlanSelected(planCode: string): boolean {
    return this.form.controls.planCode.value === planCode;
  }

  async onSave(): Promise<void> {
    const tenant = this.tenant();

    if (!tenant || this.form.invalid || this.store.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const rawValue = this.form.getRawValue();

    const formValue: AssignSubscriptionFormValue = {
      planCode: rawValue.planCode,
      overrideTrialDays:
        rawValue.overrideTrialDays === null ||
        rawValue.overrideTrialDays === undefined ||
        rawValue.overrideTrialDays === ("" as never)
          ? null
          : Number(rawValue.overrideTrialDays),
    };

    const assigned = await this.store.assign(tenant.id, formValue);

    if (!assigned) {
      toast("No se pudo asignar el plan", {
        description:
          this.store.submitError()?.message || "Ocurrió un error inesperado.",
      });
      return;
    }

    toast("Plan asignado correctamente", {
      description: `${assigned.planName} fue asignado a ${assigned.tenantName}.`,
    });

    this.saved.emit();
    this.onClosed();
  }

  onCancel(): void {
    this.onClosed();
  }
}
