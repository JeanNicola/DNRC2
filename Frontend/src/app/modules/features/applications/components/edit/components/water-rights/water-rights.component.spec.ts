import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsComponent } from './water-rights.component';

describe('WaterRightsComponent', () => {
  let component: WaterRightsComponent;
  let fixture: ComponentFixture<WaterRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WaterRightsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
