import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PublicNotFoundPage } from "./public-not-found-page";

describe("PublicNotFoundPage", () => {
  let component: PublicNotFoundPage;
  let fixture: ComponentFixture<PublicNotFoundPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicNotFoundPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PublicNotFoundPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
