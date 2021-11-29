import { TestBed } from '@angular/core/testing';

import { PaymentOriginsService } from './payment-origins.service';

describe('PaymentOriginsService', () => {
  let service: PaymentOriginsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentOriginsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
