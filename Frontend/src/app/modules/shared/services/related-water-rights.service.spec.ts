import { TestBed } from '@angular/core/testing';

import { RelatedWaterRightsService } from './related-water-rights.service';

describe('RelatedWaterRightsService', () => {
  let service: RelatedWaterRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedWaterRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
