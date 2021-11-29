import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedRightsTableComponent } from './related-rights-table.component';

describe('RelatedRightsTableComponent', () => {
  let component: RelatedRightsTableComponent;
  let fixture: ComponentFixture<RelatedRightsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedRightsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedRightsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
