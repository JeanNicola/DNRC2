import { TestBed } from '@angular/core/testing';

import { RelatedRightsForVersionService } from './related-rights-for-version.service';

describe('RelatedRightsForVersionService', () => {
  let service: RelatedRightsForVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedRightsForVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
