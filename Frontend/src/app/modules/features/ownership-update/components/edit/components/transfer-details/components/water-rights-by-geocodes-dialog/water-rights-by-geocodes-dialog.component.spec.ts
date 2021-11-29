import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsByGeocodesDialogComponent } from './water-rights-by-geocodes-dialog.component';

describe('WaterRightsByGeocodesDialogComponent', () => {
  let component: WaterRightsByGeocodesDialogComponent;
  let fixture: ComponentFixture<WaterRightsByGeocodesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WaterRightsByGeocodesDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsByGeocodesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
