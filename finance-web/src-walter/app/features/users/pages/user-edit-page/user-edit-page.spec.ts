import { ComponentFixture, TestBed } from "@angular/core/testing";

import { UserEditPage } from "./user-edit-page";

describe("UserEditPage", () => {
  let component: UserEditPage;
  let fixture: ComponentFixture<UserEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(UserEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
