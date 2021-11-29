import { TestBed } from '@angular/core/testing';

import { RelationshipTypesService } from './relationship-types.service';

describe('RelationshipTypesService', () => {
  let service: RelationshipTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
