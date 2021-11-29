import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterRightsDialogComponent } from './water-rights-dialog.component';

describe('WaterRightsDialogComponent', () => {
  let component: WaterRightsDialogComponent;
  let fixture: ComponentFixture<WaterRightsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WaterRightsDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterRightsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
