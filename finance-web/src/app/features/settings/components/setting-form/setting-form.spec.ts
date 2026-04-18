import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingForm } from "./setting-form";

describe("SettingForm", () => {
  let component: SettingForm;
  let fixture: ComponentFixture<SettingForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingForm],
    }).compileComponents();

    fixture = TestBed.createComponent(SettingForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
