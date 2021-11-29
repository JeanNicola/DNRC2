import { TestBed } from '@angular/core/testing';

import { PopulateByGeocodesService } from './populate-by-geocodes.service';

describe('PopulateByGeocodesService', () => {
  let service: PopulateByGeocodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PopulateByGeocodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
