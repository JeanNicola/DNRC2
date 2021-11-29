import { TestBed } from '@angular/core/testing';

import { WaterRightsService } from './water-rights.service';

describe('WaterRightsService', () => {
  let service: WaterRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WaterRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
