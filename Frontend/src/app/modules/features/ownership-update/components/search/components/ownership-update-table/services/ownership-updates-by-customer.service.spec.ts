import { TestBed } from '@angular/core/testing';

import { OwnershipUpdatesByCustomerService } from './ownership-updates-by-customer.service';

describe('OwnershipUpdatesByCustomerService', () => {
  let service: OwnershipUpdatesByCustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdatesByCustomerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
