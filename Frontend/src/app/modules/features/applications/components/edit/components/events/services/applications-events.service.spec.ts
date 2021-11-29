import { TestBed } from '@angular/core/testing';

import { ApplicationsEventsService } from './applications-events.service';

describe('ApplicationsEventsService', () => {
  let service: ApplicationsEventsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsEventsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
