import { TestBed } from '@angular/core/testing';

import { FeeSummaryService } from './fee-summary.service';

describe('FeeSummaryService', () => {
  let service: FeeSummaryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeeSummaryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
