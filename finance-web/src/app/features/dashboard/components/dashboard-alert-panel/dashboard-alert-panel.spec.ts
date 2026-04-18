import { ComponentFixture, TestBed } from "@angular/core/testing";

import { DashboardAlertPanel } from "./dashboard-alert-panel";

describe("DashboardAlertPanel", () => {
  let component: DashboardAlertPanel;
  let fixture: ComponentFixture<DashboardAlertPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardAlertPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardAlertPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
