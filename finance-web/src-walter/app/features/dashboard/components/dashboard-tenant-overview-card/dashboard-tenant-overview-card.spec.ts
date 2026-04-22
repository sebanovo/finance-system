import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DashboardTenantOverviewCard } from "./dashboard-tenant-overview-card";

describe("DashboardTenantOverviewCard", () => {
  let component: DashboardTenantOverviewCard;
  let fixture: ComponentFixture<DashboardTenantOverviewCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardTenantOverviewCard],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardTenantOverviewCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
