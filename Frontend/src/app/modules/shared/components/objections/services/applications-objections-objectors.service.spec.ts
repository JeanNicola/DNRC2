import { TestBed } from '@angular/core/testing';

import { ApplicationsObjectionsObjectorsService } from './applications-objections-objectors.service';

describe('ApplicationsObjectionsObjectorsService', () => {
  let service: ApplicationsObjectionsObjectorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsObjectionsObjectorsService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
