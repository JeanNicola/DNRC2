import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightRowComponent } from './water-right-row.component';

describe('WaterRightRowComponent', () => {
  let component: WaterRightRowComponent;
  let fixture: ComponentFixture<WaterRightRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WaterRightRowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
