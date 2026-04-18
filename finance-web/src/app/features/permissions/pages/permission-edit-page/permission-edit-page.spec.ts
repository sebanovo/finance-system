import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PermissionEditPage } from "./permission-edit-page";

describe("PermissionEditPage", () => {
  let component: PermissionEditPage;
  let fixture: ComponentFixture<PermissionEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermissionEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PermissionEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
