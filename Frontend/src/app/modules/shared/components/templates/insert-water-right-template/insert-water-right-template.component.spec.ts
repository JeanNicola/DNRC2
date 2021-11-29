import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertWaterRightTemplateComponent } from './insert-water-right-template.component';

describe('InsertWaterRightTemplateComponent', () => {
  let component: InsertWaterRightTemplateComponent;
  let fixture: ComponentFixture<InsertWaterRightTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertWaterRightTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertWaterRightTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
