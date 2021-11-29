import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataFieldErrorsComponent } from './data-field-errors.component';

describe('DataFieldErrorsComponent', () => {
  let component: DataFieldErrorsComponent;
  let fixture: ComponentFixture<DataFieldErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DataFieldErrorsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataFieldErrorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
