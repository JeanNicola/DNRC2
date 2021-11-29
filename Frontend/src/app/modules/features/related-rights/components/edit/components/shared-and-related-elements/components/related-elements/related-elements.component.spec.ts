import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RelatedElementsComponent } from './related-elements.component';

describe('RelatedElementsComponent', () => {
  let component: RelatedElementsComponent;
  let fixture: ComponentFixture<RelatedElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RelatedElementsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
