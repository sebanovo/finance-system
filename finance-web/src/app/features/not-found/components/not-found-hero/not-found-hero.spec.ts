import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NotFoundHero } from "./not-found-hero";

describe("NotFoundHero", () => {
  let component: NotFoundHero;
  let fixture: ComponentFixture<NotFoundHero>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotFoundHero],
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundHero);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
