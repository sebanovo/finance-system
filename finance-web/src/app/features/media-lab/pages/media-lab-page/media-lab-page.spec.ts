import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MediaLabPage } from "./media-lab-page";

describe("MediaLabPage", () => {
  let component: MediaLabPage;
  let fixture: ComponentFixture<MediaLabPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MediaLabPage],
    }).compileComponents();

    fixture = TestBed.createComponent(MediaLabPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
