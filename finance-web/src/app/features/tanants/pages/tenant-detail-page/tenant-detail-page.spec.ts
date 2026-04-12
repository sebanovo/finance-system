import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantDetailPage } from "./tenant-detail-page";

describe("TenantDetailPage", () => {
  let component: TenantDetailPage;
  let fixture: ComponentFixture<TenantDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantDetailPage],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantDetailPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
