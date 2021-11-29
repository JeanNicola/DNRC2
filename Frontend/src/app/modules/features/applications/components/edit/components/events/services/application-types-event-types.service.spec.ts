import { TestBed } from '@angular/core/testing';

import { ApplicationTypesEventTypesService } from './application-types-event-types.service';

describe('ApplicationTypesEventTypesService', () => {
  let service: ApplicationTypesEventTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationTypesEventTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
