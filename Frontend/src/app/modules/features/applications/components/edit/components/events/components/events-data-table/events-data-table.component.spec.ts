import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsDataTableComponent } from './events-data-table.component';

describe('EventsDataTableComponent', () => {
  let component: EventsDataTableComponent;
  let fixture: ComponentFixture<EventsDataTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EventsDataTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsDataTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    void expect(component).toBeTruthy();
  });
});
