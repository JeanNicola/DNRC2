import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreInfoDialogComponent } from './more-info-dialog.component';

describe('MoreInfoDialogComponent', () => {
  let component: MoreInfoDialogComponent;
  let fixture: ComponentFixture<MoreInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MoreInfoDialogComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoreInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
