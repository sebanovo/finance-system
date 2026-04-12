import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SubscriptionListPage } from "./subscription-list-page";

describe("SubscriptionListPage", () => {
  let component: SubscriptionListPage;
  let fixture: ComponentFixture<SubscriptionListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(SubscriptionListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
