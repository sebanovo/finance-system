import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantEmptyState } from "./tenant-empty-state";

describe("TenantEmptyState", () => {
  let component: TenantEmptyState;
  let fixture: ComponentFixture<TenantEmptyState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantEmptyState],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantEmptyState);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
