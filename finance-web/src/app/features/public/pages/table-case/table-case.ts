import { Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { lucideEllipsis, lucideLock, lucideMail } from "@ng-icons/lucide";
import { HlmButtonImports } from "@shared/ui/button";
import { HlmCardImports } from "@shared/ui/card";
import { HlmDropdownMenuImports } from "@shared/ui/dropdown-menu";
import { HlmFieldImports } from "@shared/ui/field";
import { HlmInputImports } from "@shared/ui/input";
import { HlmInputGroupImports } from "@shared/ui/input-group";

@Component({
  selector: "app-table-case",
  imports: [
    ReactiveFormsModule,
    HlmInputGroupImports,
    HlmCardImports,
    NgIcon,
    HlmFieldImports,
    HlmInputImports,
    HlmButtonImports,
    HlmDropdownMenuImports,
  ],
  providers: [provideIcons({ lucideEllipsis, lucideMail, lucideLock })],
  templateUrl: "./table-case.html",
  styleUrl: "./table-case.css",
})
export class TableCase {
  private readonly _fb = inject(FormBuilder);

  readonly form = this._fb.group({
    email: ["", [Validators.required, Validators.email]],
    password: [
      "",
      [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/),
      ],
    ],
    title: [
      "",
      [Validators.required, Validators.minLength(5), Validators.maxLength(32)],
    ],
    description: [
      "",
      [
        Validators.required,
        Validators.minLength(20),
        Validators.maxLength(100),
      ],
    ],
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    console.log(
      "You submitted the following values:",
      JSON.stringify(this.form.value, null, 2)
    );
  }
}
