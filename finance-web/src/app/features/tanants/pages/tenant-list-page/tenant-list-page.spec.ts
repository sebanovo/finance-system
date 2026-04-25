import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantListPage } from "./tenant-list-page";

describe("TenantListPage", () => {
  let component: TenantListPage;
  let fixture: ComponentFixture<TenantListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
