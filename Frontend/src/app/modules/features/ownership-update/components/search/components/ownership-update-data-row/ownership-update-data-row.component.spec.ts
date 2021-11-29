import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipUpdateDataRowComponent } from './ownership-update-data-row.component';

describe('OwnershipUpdateDataRowComponent', () => {
  let component: OwnershipUpdateDataRowComponent;
  let fixture: ComponentFixture<OwnershipUpdateDataRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnershipUpdateDataRowComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnershipUpdateDataRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
