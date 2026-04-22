import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PlanListPage } from "./plan-list-page";

describe("PlanListPage", () => {
  let component: PlanListPage;
  let fixture: ComponentFixture<PlanListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
