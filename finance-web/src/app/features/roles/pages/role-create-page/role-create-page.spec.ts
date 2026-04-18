import { ComponentFixture, TestBed } from "@angular/core/testing";

import { RoleCreatePage } from "./role-create-page";

describe("RoleCreatePage", () => {
  let component: RoleCreatePage;
  let fixture: ComponentFixture<RoleCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(RoleCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
