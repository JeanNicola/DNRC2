import { TestBed } from '@angular/core/testing';

import { OwnershipUpdateForContactService } from './ownership-update.service';

describe('OwnershipUpdateForContactService', () => {
  let service: OwnershipUpdateForContactService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdateForContactService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
