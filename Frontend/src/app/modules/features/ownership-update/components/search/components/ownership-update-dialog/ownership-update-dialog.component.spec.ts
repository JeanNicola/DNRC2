import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipUpdateDialogComponent } from './ownership-update-dialog.component';

describe('OwnershipUpdateDialogComponent', () => {
  let component: OwnershipUpdateDialogComponent;
  let fixture: ComponentFixture<OwnershipUpdateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnershipUpdateDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnershipUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
