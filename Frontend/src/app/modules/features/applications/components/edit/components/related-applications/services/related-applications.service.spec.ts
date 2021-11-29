import { TestBed } from '@angular/core/testing';

import { RelatedApplicationsService } from './related-applications.service';

describe('RelatedApplicationsService', () => {
  let service: RelatedApplicationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedApplicationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
