import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantDialog } from "./tenant-dialog";

describe("TenantDialog", () => {
  let component: TenantDialog;
  let fixture: ComponentFixture<TenantDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
