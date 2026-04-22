import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DashboardMetricCard } from "./dashboard-metric-card";

describe("DashboardMetricCard", () => {
  let component: DashboardMetricCard;
  let fixture: ComponentFixture<DashboardMetricCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardMetricCard],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardMetricCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
