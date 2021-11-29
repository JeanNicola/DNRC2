import { TestBed } from '@angular/core/testing';

import { ActiveApplicationsService } from './active-applications.service';

describe('ActiveApplicationsService', () => {
  let service: ActiveApplicationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActiveApplicationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
