import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsTableComponent } from './water-rights-table.component';

describe('WaterRightsTableComponent', () => {
  let component: WaterRightsTableComponent;
  let fixture: ComponentFixture<WaterRightsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WaterRightsTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
