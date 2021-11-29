import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectionCodeTableComponent } from './selection-code-table.component';

describe('SelectionCodeTableComponent', () => {
  let component: SelectionCodeTableComponent;
  let fixture: ComponentFixture<SelectionCodeTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectionCodeTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectionCodeTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
