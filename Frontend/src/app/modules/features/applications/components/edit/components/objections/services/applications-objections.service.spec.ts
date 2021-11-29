import { TestBed } from '@angular/core/testing';

import { ApplicationsObjectionsService } from './applications-objections.service';

describe('ApplicationsObjectionsService', () => {
  let service: ApplicationsObjectionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsObjectionsService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
