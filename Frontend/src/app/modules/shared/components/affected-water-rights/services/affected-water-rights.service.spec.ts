import { TestBed } from '@angular/core/testing';

import { AffectedWaterRightsService } from './affected-water-rights.service';

describe('AffectedWaterRightsService', () => {
  let service: AffectedWaterRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AffectedWaterRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
