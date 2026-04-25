import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlanEditPage } from "./plan-edit-page";

describe("PlanEditPage", () => {
  let component: PlanEditPage;
  let fixture: ComponentFixture<PlanEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
