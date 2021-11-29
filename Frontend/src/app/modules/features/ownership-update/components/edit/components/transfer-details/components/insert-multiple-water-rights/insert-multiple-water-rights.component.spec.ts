import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertMultipleWaterRightsComponent } from './insert-multiple-water-rights.component';

describe('InsertMultipleWaterRightsComponent', () => {
  let component: InsertMultipleWaterRightsComponent;
  let fixture: ComponentFixture<InsertMultipleWaterRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertMultipleWaterRightsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertMultipleWaterRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
