import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CurrentSubscriptionPage } from "./current-subscription-page";

describe("CurrentSubscriptionPage", () => {
  let component: CurrentSubscriptionPage;
  let fixture: ComponentFixture<CurrentSubscriptionPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrentSubscriptionPage],
    }).compileComponents();

    fixture = TestBed.createComponent(CurrentSubscriptionPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
