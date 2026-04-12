import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TenantTable } from "./tenant-table";

describe("TenantTable", () => {
  let component: TenantTable;
  let fixture: ComponentFixture<TenantTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantTable],
    }).compileComponents();

    fixture = TestBed.createComponent(TenantTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
