import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeocodesTableComponent } from './geocodes-table.component';

describe('GeocodesTableComponent', () => {
  let component: GeocodesTableComponent;
  let fixture: ComponentFixture<GeocodesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeocodesTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeocodesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
