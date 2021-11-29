import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DorPaymentsComponent } from './dor-payments.component';

describe('DorPaymentsComponent', () => {
  let component: DorPaymentsComponent;
  let fixture: ComponentFixture<DorPaymentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DorPaymentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DorPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
