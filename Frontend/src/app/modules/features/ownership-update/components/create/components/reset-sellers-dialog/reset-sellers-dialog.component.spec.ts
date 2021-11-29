import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetSellersDialogComponent } from './reset-sellers-dialog.component';

describe('ResetSellersDialogComponent', () => {
  let component: ResetSellersDialogComponent;
  let fixture: ComponentFixture<ResetSellersDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResetSellersDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetSellersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
