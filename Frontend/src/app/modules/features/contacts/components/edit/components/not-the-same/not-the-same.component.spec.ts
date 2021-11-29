import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotTheSameComponent } from './not-the-same.component';

describe('NotTheSameComponent', () => {
  let component: NotTheSameComponent;
  let fixture: ComponentFixture<NotTheSameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotTheSameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotTheSameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
