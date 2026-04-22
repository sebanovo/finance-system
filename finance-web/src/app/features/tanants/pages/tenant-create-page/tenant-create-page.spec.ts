import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantCreatePage } from "./tenant-create-page";

describe("TenantCreatePage", () => {
  let component: TenantCreatePage;
  let fixture: ComponentFixture<TenantCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
