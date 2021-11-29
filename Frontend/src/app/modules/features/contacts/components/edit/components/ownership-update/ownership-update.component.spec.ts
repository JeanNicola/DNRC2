import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnershipUpdateComponent } from './ownership-update.component';

describe('OwnershipUpdateComponent', () => {
  let component: OwnershipUpdateComponent;
  let fixture: ComponentFixture<OwnershipUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OwnershipUpdateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnershipUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
