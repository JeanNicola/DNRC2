import { TestBed } from '@angular/core/testing';

import { GeocodesService } from './geocodes.service';

describe('GeocodesService', () => {
  let service: GeocodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GeocodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
