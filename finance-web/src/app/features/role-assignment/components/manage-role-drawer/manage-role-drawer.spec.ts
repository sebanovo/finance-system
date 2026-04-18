import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ManageRoleDrawer } from "./manage-role-drawer";

describe("ManageRoleDrawer", () => {
  let component: ManageRoleDrawer;
  let fixture: ComponentFixture<ManageRoleDrawer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageRoleDrawer],
    }).compileComponents();

    fixture = TestBed.createComponent(ManageRoleDrawer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
