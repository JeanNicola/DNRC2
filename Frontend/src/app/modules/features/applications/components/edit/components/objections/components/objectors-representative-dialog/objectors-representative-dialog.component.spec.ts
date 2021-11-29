import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectorsRepresentativeDialogComponent } from './objectors-representative-dialog.component';

describe('ObjectorsRepresentativeDialogComponent', () => {
  let component: ObjectorsRepresentativeDialogComponent;
  let fixture: ComponentFixture<ObjectorsRepresentativeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectorsRepresentativeDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectorsRepresentativeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
