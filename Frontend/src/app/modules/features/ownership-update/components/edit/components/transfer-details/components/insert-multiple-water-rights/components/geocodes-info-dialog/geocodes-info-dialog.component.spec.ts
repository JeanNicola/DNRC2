import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeocodesInfoDialogComponent } from './geocodes-info-dialog.component';

describe('GeocodesInfoDialogComponent', () => {
  let component: GeocodesInfoDialogComponent;
  let fixture: ComponentFixture<GeocodesInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeocodesInfoDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeocodesInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
