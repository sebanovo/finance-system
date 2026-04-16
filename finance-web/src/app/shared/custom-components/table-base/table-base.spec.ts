import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TableBase } from "./table-base";

describe("TableBase", () => {
  let component: TableBase;
  let fixture: ComponentFixture<TableBase>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableBase],
    }).compileComponents();

    fixture = TestBed.createComponent(TableBase);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
