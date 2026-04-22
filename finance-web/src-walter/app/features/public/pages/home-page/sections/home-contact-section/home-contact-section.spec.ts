import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeContactSection } from './home-contact-section';

describe('HomeContactSection', () => {
  let component: HomeContactSection;
  let fixture: ComponentFixture<HomeContactSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeContactSection],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeContactSection);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
