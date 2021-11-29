import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuyersTableComponent } from './buyers-table.component';

describe('BuyersTableComponent', () => {
  let component: BuyersTableComponent;
  let fixture: ComponentFixture<BuyersTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BuyersTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BuyersTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
