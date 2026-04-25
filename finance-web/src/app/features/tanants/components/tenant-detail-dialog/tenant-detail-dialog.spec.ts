import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantDetailDialog } from "./tenant-detail-dialog";

describe("TenantDetailDialog", () => {
  let component: TenantDetailDialog;
  let fixture: ComponentFixture<TenantDetailDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantDetailDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantDetailDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
