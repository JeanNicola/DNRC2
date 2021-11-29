import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertWaterRightComponent } from './insert-water-right.component';

describe('InsertWaterRightComponent', () => {
  let component: InsertWaterRightComponent;
  let fixture: ComponentFixture<InsertWaterRightComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InsertWaterRightComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertWaterRightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
