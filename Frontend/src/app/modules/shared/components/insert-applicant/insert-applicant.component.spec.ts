import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertApplicantComponent } from './insert-applicant.component';

describe('InsertApplicantComponent', () => {
  let component: InsertApplicantComponent;
  let fixture: ComponentFixture<InsertApplicantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertApplicantComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertApplicantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
