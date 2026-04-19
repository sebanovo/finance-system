import { ComponentFixture, TestBed } from "@angular/core/testing";

import { RoleEditPage } from "./role-edit-page";

describe("RoleEditPage", () => {
  let component: RoleEditPage;
  let fixture: ComponentFixture<RoleEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RoleEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
