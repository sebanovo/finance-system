import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantFilters } from "./tenant-filters";

describe("TenantFilters", () => {
  let component: TenantFilters;
  let fixture: ComponentFixture<TenantFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantFilters],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantFilters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
