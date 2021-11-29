import { TestBed } from '@angular/core/testing';

import { RelatedWaterRightVersionsService } from './related-water-right-versions.service';

describe('RelatedWaterRightVersionsService', () => {
  let service: RelatedWaterRightVersionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedWaterRightVersionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
