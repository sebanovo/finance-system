import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PermissionDetailDialog } from "./permission-detail-dialog";

describe("PermissionDetailDialog", () => {
  let component: PermissionDetailDialog;
  let fixture: ComponentFixture<PermissionDetailDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermissionDetailDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(PermissionDetailDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
