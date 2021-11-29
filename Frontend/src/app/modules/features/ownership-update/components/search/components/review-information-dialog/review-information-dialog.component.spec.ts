import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewInformationDialogComponent } from './review-information-dialog.component';

describe('ReviewInformationDialogComponent', () => {
  let component: ReviewInformationDialogComponent;
  let fixture: ComponentFixture<ReviewInformationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReviewInformationDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewInformationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
