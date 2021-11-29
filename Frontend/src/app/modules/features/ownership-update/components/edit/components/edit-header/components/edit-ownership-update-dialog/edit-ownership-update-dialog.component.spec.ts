import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditOwnershipUpdateDialogComponent } from './edit-ownership-update-dialog.component';

describe('EditOwnershipUpdateDialogComponent', () => {
  let component: EditOwnershipUpdateDialogComponent;
  let fixture: ComponentFixture<EditOwnershipUpdateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditOwnershipUpdateDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditOwnershipUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
