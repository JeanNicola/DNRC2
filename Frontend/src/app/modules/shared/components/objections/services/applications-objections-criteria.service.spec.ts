import { TestBed } from '@angular/core/testing';

import { ApplicationsObjectionsCriteriaService } from './applications-objections-criteria.service';

describe('ApplicationsObjectionsCriteriaService', () => {
  let service: ApplicationsObjectionsCriteriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationsObjectionsCriteriaService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
