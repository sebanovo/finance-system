import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TableCase } from "./table-case";

describe("TableCase", () => {
  let component: TableCase;
  let fixture: ComponentFixture<TableCase>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableCase],
    }).compileComponents();

    fixture = TestBed.createComponent(TableCase);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
