import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedRightsComponent } from './related-rights.component';

describe('RelatedRightsComponent', () => {
  let component: RelatedRightsComponent;
  let fixture: ComponentFixture<RelatedRightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedRightsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
