import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantSettingPage } from "./tenant-setting-page";

describe("TenantSettingPage", () => {
  let component: TenantSettingPage;
  let fixture: ComponentFixture<TenantSettingPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantSettingPage],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantSettingPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
