import { ComponentFixture, TestBed } from "@angular/core/testing";

import { UserDetailDialog } from "./user-detail-dialog";

describe("UserDetailDialog", () => {
  let component: UserDetailDialog;
  let fixture: ComponentFixture<UserDetailDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserDetailDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(UserDetailDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
