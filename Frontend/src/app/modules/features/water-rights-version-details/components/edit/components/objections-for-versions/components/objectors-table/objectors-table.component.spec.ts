import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectorsTableComponent } from './objectors-table.component';

describe('ObjectorsTableComponent', () => {
  let component: ObjectorsTableComponent;
  let fixture: ComponentFixture<ObjectorsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectorsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectorsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
