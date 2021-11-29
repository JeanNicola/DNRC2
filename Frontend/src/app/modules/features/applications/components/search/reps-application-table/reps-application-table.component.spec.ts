import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepsApplicationTableComponent } from './reps-application-table.component';

describe('RepsApplicationTableComponent', () => {
  let component: RepsApplicationTableComponent;
  let fixture: ComponentFixture<RepsApplicationTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepsApplicationTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepsApplicationTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
