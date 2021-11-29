import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewInformationComponent } from './review-information.component';

describe('ReviewInformationComponent', () => {
  let component: ReviewInformationComponent;
  let fixture: ComponentFixture<ReviewInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReviewInformationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
