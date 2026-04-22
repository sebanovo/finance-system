import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SubscriptionDetailDialog } from "./subscription-detail-dialog";

describe("SubscriptionDetailDialog", () => {
  let component: SubscriptionDetailDialog;
  let fixture: ComponentFixture<SubscriptionDetailDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionDetailDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(SubscriptionDetailDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
