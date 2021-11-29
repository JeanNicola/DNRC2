import { TestBed } from '@angular/core/testing';

import { FeeStatusService } from './fee-status.service';

describe('FeeStatusService', () => {
  let service: FeeStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeeStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
