import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventSubtypeComponent } from './event-subtype.component';

describe('EventSubtypeComponent', () => {
  let component: EventSubtypeComponent;
  let fixture: ComponentFixture<EventSubtypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventSubtypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventSubtypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
