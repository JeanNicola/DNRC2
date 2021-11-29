import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeDescriptionConfirmDialogComponent } from './change-description-confirm-dialog.component';

describe('DeleteDialogComponent', () => {
  let component: ChangeDescriptionConfirmDialogComponent;
  let fixture: ComponentFixture<ChangeDescriptionConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangeDescriptionConfirmDialogComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeDescriptionConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
