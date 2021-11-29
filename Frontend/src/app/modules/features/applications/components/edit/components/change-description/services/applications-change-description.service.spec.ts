import { TestBed } from '@angular/core/testing';

import { ApplicationsChangeDescriptionService } from './applications-change-description.service';

describe('ApplicationsChangeDescriptionService', () => {
  let service: ApplicationsChangeDescriptionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsChangeDescriptionService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
