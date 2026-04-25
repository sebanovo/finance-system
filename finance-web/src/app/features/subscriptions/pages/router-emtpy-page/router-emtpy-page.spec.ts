import { ComponentFixture, TestBed } from "@angular/core/testing";

import { RouterEmtpyPage } from "./router-emtpy-page";

describe("RouterEmtpyPage", () => {
  let component: RouterEmtpyPage;
  let fixture: ComponentFixture<RouterEmtpyPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterEmtpyPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RouterEmtpyPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
