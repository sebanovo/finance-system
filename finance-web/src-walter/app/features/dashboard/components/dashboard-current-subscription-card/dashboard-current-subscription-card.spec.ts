import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DashboardCurrentSubscriptionCard } from "./dashboard-current-subscription-card";

describe("DashboardCurrentSubscriptionCard", () => {
  let component: DashboardCurrentSubscriptionCard;
  let fixture: ComponentFixture<DashboardCurrentSubscriptionCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardCurrentSubscriptionCard],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardCurrentSubscriptionCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
