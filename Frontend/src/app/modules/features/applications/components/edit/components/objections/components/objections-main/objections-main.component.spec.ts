import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsMainComponent } from './objections-main.component';

describe('ObjectionsMainComponent', () => {
  let component: ObjectionsMainComponent;
  let fixture: ComponentFixture<ObjectionsMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ObjectionsMainComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
