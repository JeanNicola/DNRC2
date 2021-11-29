import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecalculateFeeDueComponent } from './recalculate-fee-due.component';

describe('RecalculateFeeDueComponent', () => {
  let component: RecalculateFeeDueComponent;
  let fixture: ComponentFixture<RecalculateFeeDueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RecalculateFeeDueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecalculateFeeDueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
