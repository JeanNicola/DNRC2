import { TestBed } from '@angular/core/testing';

import { AplicationsApplicantsService } from './applications-applicants.service';

describe('AplicationsApplicantsService', () => {
  let service: AplicationsApplicantsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AplicationsApplicantsService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
