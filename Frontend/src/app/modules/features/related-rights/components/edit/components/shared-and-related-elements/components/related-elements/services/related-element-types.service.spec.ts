import { TestBed } from '@angular/core/testing';

import { RelatedElementTypesService } from './related-element-types.service';

describe('RelatedElementTypesService', () => {
  let service: RelatedElementTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedElementTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
