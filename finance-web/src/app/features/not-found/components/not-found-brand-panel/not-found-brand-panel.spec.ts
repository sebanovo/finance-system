import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NotFoundBrandPanel } from "./not-found-brand-panel";

describe("NotFoundBrandPanel", () => {
  let component: NotFoundBrandPanel;
  let fixture: ComponentFixture<NotFoundBrandPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotFoundBrandPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundBrandPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
