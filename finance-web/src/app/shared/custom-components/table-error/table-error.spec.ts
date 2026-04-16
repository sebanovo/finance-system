import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TableError } from "./table-error";

describe("TableError", () => {
  let component: TableError;
  let fixture: ComponentFixture<TableError>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableError],
    }).compileComponents();

    fixture = TestBed.createComponent(TableError);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
