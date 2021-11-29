import { TestBed } from '@angular/core/testing';

import { OwnershipUpdateSellersService } from './ownership-update-sellers.service';

describe('OwnershipUpdateSellersService', () => {
  let service: OwnershipUpdateSellersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdateSellersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
