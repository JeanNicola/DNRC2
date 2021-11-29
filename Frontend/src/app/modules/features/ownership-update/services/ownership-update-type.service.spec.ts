import { TestBed } from '@angular/core/testing';

import { OwnershipUpdateTypeService } from './ownership-update-type.service';

describe('OwnershipUpdateTypeService', () => {
  let service: OwnershipUpdateTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnershipUpdateTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
