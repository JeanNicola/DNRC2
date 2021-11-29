import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectionDataTableComponent } from './selection-data-table.component';

describe('CodeTableWithCheckboxesComponent', () => {
  let component: SelectionDataTableComponent;
  let fixture: ComponentFixture<SelectionDataTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectionDataTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectionDataTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
