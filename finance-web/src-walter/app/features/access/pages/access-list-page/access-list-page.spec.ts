import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AccessListPage } from "./access-list-page";

describe("AccessListPage", () => {
  let component: AccessListPage;
  let fixture: ComponentFixture<AccessListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccessListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AccessListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
