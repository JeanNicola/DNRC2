import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedElementsTableComponent } from './related-elements-table.component';

describe('RelatedElementsTableComponent', () => {
  let component: RelatedElementsTableComponent;
  let fixture: ComponentFixture<RelatedElementsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedElementsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedElementsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
