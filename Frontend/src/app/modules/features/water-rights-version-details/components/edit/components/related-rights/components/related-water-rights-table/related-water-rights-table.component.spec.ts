import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedWaterRightsTableComponent } from './related-water-rights-table.component';

describe('RelatedWaterRightsTableComponent', () => {
  let component: RelatedWaterRightsTableComponent;
  let fixture: ComponentFixture<RelatedWaterRightsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedWaterRightsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedWaterRightsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
