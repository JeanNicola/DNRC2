import { TestBed } from '@angular/core/testing';

import { AplicationsViewOwnerService } from './applications-view-owner.service';

describe('AplicationsViewOwnerService', () => {
  let service: AplicationsViewOwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AplicationsViewOwnerService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
