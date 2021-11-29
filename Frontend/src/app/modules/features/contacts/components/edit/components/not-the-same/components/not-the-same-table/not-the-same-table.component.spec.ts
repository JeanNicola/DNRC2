import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotTheSameTableComponent } from './not-the-same-table.component';

describe('NotTheSameTableComponent', () => {
  let component: NotTheSameTableComponent;
  let fixture: ComponentFixture<NotTheSameTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotTheSameTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotTheSameTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
