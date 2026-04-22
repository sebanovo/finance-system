import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlanTable } from "./plan-table";

describe("PlanTable", () => {
  let component: PlanTable;
  let fixture: ComponentFixture<PlanTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanTable],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
