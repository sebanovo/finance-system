import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlanDetailDialog } from "./plan-detail-dialog";

describe("PlanDetailDialog", () => {
  let component: PlanDetailDialog;
  let fixture: ComponentFixture<PlanDetailDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanDetailDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanDetailDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
