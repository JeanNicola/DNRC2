import { TestBed } from '@angular/core/testing';

import { OwnershipUpdateService } from './ownership-update.service';

describe('OwnershipUpdateService', () => {
  let service: OwnershipUpdateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
