import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsUpdateDialogComponent } from './water-rights-update-dialog.component';

describe('WaterRightsUpdateDialogComponent', () => {
  let component: WaterRightsUpdateDialogComponent;
  let fixture: ComponentFixture<WaterRightsUpdateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WaterRightsUpdateDialogComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
