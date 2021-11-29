import { TestBed } from '@angular/core/testing';

import { FeeLetterServiceService } from './fee-letter-service.service';

describe('FeeLetterServiceService', () => {
  let service: FeeLetterServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeeLetterServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
