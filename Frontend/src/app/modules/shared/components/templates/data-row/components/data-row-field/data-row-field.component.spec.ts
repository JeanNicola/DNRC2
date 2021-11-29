import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataRowFieldComponent } from './data-row-field.component';

describe('DataRowFieldComponent', () => {
  let component: DataRowFieldComponent;
  let fixture: ComponentFixture<DataRowFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataRowFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataRowFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
