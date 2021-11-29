import { TestBed } from '@angular/core/testing';

import { RelatedRightsService } from './related-rights.service';

describe('RelatedRightsService', () => {
  let service: RelatedRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
