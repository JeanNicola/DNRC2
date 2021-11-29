import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectionsTableComponent } from './objections-table.component';

describe('ObjectionsTableComponent', () => {
  let component: ObjectionsTableComponent;
  let fixture: ComponentFixture<ObjectionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectionsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
