import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadOnlyFieldComponent } from './read-only-field.component';

describe('ReadOnlyFieldComponent', () => {
  let component: ReadOnlyFieldComponent;
  let fixture: ComponentFixture<ReadOnlyFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReadOnlyFieldComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadOnlyFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
