import { TestBed } from '@angular/core/testing';

import { PopulateBySellersService } from './populate-by-sellers.service';

describe('PopulateBySellersService', () => {
  let service: PopulateBySellersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PopulateBySellersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
