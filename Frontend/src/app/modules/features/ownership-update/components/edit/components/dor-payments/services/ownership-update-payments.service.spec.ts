import { TestBed } from '@angular/core/testing';

import { OwnershipUpdatePaymentsService } from './ownership-update-payments.service';

describe('OwnershipUpdatePaymentsService', () => {
  let service: OwnershipUpdatePaymentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdatePaymentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
