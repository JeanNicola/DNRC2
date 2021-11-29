import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeDescriptionUpdateDialogComponent } from './change-description-update-dialog.component';

describe('ChangeDescriptionUpdateDialogComponent', () => {
  let component: ChangeDescriptionUpdateDialogComponent;
  let fixture: ComponentFixture<ChangeDescriptionUpdateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeDescriptionUpdateDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeDescriptionUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
