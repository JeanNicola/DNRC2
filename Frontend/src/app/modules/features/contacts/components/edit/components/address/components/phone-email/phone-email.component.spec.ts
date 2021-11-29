import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhoneEmailComponent } from './phone-email.component';

describe('PhoneEmailComponent', () => {
  let component: PhoneEmailComponent;
  let fixture: ComponentFixture<PhoneEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PhoneEmailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PhoneEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
