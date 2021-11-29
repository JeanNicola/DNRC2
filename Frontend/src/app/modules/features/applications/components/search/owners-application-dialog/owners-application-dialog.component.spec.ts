import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnersApplicationDialogComponent } from './owners-application-dialog.component';

describe('OwnersApplicationDialogComponent', () => {
  let component: OwnersApplicationDialogComponent;
  let fixture: ComponentFixture<OwnersApplicationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnersApplicationDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnersApplicationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
