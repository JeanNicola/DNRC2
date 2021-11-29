import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InsertSellerBuyerComponent } from './insert-seller-buyer.component';

describe('InsertSellerBuyerComponent', () => {
  let component: InsertSellerBuyerComponent;
  let fixture: ComponentFixture<InsertSellerBuyerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InsertSellerBuyerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InsertSellerBuyerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
