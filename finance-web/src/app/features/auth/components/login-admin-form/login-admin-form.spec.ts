import { ComponentFixture, TestBed } from "@angular/core/testing";

import { LoginAdminForm } from "./login-admin-form";

describe("LoginAdminForm", () => {
  let component: LoginAdminForm;
  let fixture: ComponentFixture<LoginAdminForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginAdminForm],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginAdminForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
