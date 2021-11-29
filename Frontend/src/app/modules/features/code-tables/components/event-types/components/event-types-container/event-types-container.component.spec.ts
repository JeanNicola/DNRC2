import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTypesContainerComponent } from './event-types-container.component';

describe('EventTypesContainerComponent', () => {
  let component: EventTypesContainerComponent;
  let fixture: ComponentFixture<EventTypesContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventTypesContainerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventTypesContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
