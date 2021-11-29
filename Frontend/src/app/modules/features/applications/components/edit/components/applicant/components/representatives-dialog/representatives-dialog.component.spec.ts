import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepresentativesDialogComponent } from './representatives-dialog.component';

describe('RepresentativesDialogComponent', () => {
  let component: RepresentativesDialogComponent;
  let fixture: ComponentFixture<RepresentativesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepresentativesDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepresentativesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
