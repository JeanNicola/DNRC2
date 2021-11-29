import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipUpdateTableComponent } from './ownership-update-table.component';

describe('OwnershipUpdateTableComponent', () => {
  let component: OwnershipUpdateTableComponent;
  let fixture: ComponentFixture<OwnershipUpdateTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnershipUpdateTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnershipUpdateTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
