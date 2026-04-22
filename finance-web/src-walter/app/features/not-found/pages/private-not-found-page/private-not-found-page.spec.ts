import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PrivateNotFoundPage } from "./private-not-found-page";

describe("PrivateNotFoundPage", () => {
  let component: PrivateNotFoundPage;
  let fixture: ComponentFixture<PrivateNotFoundPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivateNotFoundPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PrivateNotFoundPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
