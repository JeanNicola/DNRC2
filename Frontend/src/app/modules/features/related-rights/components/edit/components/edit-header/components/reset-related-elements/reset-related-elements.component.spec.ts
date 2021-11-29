import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetRelatedElementsComponent } from './reset-related-elements.component';

describe('ResetRelatedElementsComponent', () => {
  let component: ResetRelatedElementsComponent;
  let fixture: ComponentFixture<ResetRelatedElementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResetRelatedElementsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetRelatedElementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
