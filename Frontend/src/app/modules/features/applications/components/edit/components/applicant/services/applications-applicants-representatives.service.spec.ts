import { TestBed } from '@angular/core/testing';

import { ApplicationsApplicantsRepresentativesService } from './applications-applicants-representatives.service';

describe('ApplicationsApplicantsRepresentativesService', () => {
  let service: ApplicationsApplicantsRepresentativesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsApplicantsRepresentativesService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
