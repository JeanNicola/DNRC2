import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellersTableComponent } from './sellers-table.component';

describe('SellersTableComponent', () => {
  let component: SellersTableComponent;
  let fixture: ComponentFixture<SellersTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SellersTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SellersTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
