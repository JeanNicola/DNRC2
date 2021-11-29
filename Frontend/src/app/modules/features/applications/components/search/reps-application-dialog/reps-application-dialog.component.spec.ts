import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepsApplicationDialogComponent } from './reps-application-dialog.component';

describe('RepsApplicationDialogComponent', () => {
  let component: RepsApplicationDialogComponent;
  let fixture: ComponentFixture<RepsApplicationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepsApplicationDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepsApplicationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
