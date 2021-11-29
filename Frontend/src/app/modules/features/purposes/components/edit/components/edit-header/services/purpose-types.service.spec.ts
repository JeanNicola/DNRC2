import { TestBed } from '@angular/core/testing';

import { PurposeTypesService } from './purpose-types.service';

describe('PurposeTypesService', () => {
  let service: PurposeTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PurposeTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
