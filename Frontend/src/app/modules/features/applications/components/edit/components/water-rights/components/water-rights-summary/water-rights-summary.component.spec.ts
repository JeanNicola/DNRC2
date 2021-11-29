import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsSummaryComponent } from './water-rights-summary.component';

describe('WaterRightsSummaryComponent', () => {
  let component: WaterRightsSummaryComponent;
  let fixture: ComponentFixture<WaterRightsSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WaterRightsSummaryComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
