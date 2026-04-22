import { ComponentFixture, TestBed } from "@angular/core/testing";

import { UserCreatePage } from "./user-create-page";

describe("UserCreatePage", () => {
  let component: UserCreatePage;
  let fixture: ComponentFixture<UserCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(UserCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
