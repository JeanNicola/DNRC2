import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeeLetterComponent } from './fee-letter.component';

describe('FeeLetterComponent', () => {
  let component: FeeLetterComponent;
  let fixture: ComponentFixture<FeeLetterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeeLetterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeeLetterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
