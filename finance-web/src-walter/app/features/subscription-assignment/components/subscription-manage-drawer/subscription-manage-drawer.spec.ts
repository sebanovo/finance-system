import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SubscriptionManageDrawer } from "./subscription-manage-drawer";

describe("SubscriptionManageDrawer", () => {
  let component: SubscriptionManageDrawer;
  let fixture: ComponentFixture<SubscriptionManageDrawer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionManageDrawer],
    }).compileComponents();

    fixture = TestBed.createComponent(SubscriptionManageDrawer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
