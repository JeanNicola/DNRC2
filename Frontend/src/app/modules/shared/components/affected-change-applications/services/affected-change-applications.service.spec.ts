import { TestBed } from '@angular/core/testing';

import { AffectedChangeApplicationsService } from './affected-change-applications.service';

describe('AffectedChangeApplicationsService', () => {
  let service: AffectedChangeApplicationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AffectedChangeApplicationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
