import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsObjectorsComponent } from './objections-objectors.component';

describe('ObjectionsObjectorsComponent', () => {
  let component: ObjectionsObjectorsComponent;
  let fixture: ComponentFixture<ObjectionsObjectorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ObjectionsObjectorsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsObjectorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
