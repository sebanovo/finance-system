import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NoobTable } from "./noob-table";

describe("NoobTable", () => {
  let component: NoobTable;
  let fixture: ComponentFixture<NoobTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoobTable],
    }).compileComponents();

    fixture = TestBed.createComponent(NoobTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
