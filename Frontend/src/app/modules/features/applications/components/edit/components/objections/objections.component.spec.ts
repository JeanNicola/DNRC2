import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsComponent } from './objections.component';

describe('ObjectionsComponent', () => {
  let component: ObjectionsComponent;
  let fixture: ComponentFixture<ObjectionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ObjectionsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
