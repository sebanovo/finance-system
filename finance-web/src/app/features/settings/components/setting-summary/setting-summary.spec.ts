import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingSummary } from "./setting-summary";

describe("SettingSummary", () => {
  let component: SettingSummary;
  let fixture: ComponentFixture<SettingSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingSummary],
    }).compileComponents();

    fixture = TestBed.createComponent(SettingSummary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
