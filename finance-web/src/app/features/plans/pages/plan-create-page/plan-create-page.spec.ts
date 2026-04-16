import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlanCreatePage } from "./plan-create-page";

describe("PlanCreatePage", () => {
  let component: PlanCreatePage;
  let fixture: ComponentFixture<PlanCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
