import { TestBed } from '@angular/core/testing';

import { CalculateFeeDueService } from './calculate-fee-due.service';

describe('CalculateFeeDueService', () => {
  let service: CalculateFeeDueService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CalculateFeeDueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
