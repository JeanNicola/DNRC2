import { TestBed } from '@angular/core/testing';

import { OwnershipUpdateBuyersService } from './ownership-update-buyers.service';

describe('OwnershipUpdateBuyersService', () => {
  let service: OwnershipUpdateBuyersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdateBuyersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
