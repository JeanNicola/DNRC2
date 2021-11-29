import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemarksTableComponent } from './remarks-table.component';

describe('RemarksTableComponent', () => {
  let component: RemarksTableComponent;
  let fixture: ComponentFixture<RemarksTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RemarksTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RemarksTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
