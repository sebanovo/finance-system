import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PermissionCreatePage } from "./permission-create-page";

describe("PermissionCreatePage", () => {
  let component: PermissionCreatePage;
  let fixture: ComponentFixture<PermissionCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermissionCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(PermissionCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
