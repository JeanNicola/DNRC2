import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AffectedWaterRightsComponent } from './affected-water-rights.component';

describe('AffectedWaterRightsComponent', () => {
  let component: AffectedWaterRightsComponent;
  let fixture: ComponentFixture<AffectedWaterRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AffectedWaterRightsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AffectedWaterRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
