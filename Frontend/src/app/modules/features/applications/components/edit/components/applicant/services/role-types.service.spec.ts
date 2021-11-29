import { TestBed } from '@angular/core/testing';

import { RoleTypesService } from './role-types.service';

describe('RoleTypesService', () => {
  let service: RoleTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoleTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
