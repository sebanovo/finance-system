import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CurrentSubscriptionSummary } from "./current-subscription-summary";

describe("CurrentSubscriptionSummary", () => {
  let component: CurrentSubscriptionSummary;
  let fixture: ComponentFixture<CurrentSubscriptionSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrentSubscriptionSummary],
    }).compileComponents();

    fixture = TestBed.createComponent(CurrentSubscriptionSummary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
