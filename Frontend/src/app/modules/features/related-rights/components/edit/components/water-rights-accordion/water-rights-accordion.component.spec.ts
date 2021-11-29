import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsAccordionComponent } from './water-rights-accordion.component';

describe('WaterRightsAccordionComponent', () => {
  let component: WaterRightsAccordionComponent;
  let fixture: ComponentFixture<WaterRightsAccordionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WaterRightsAccordionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
