import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SubscriptionTable } from "./subscription-table";

describe("SubscriptionTable", () => {
  let component: SubscriptionTable;
  let fixture: ComponentFixture<SubscriptionTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionTable],
    }).compileComponents();

    fixture = TestBed.createComponent(SubscriptionTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
