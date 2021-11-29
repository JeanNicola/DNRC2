import { TestBed } from '@angular/core/testing';

import { RelatedElementsService } from './related-elements.service';

describe('RelatedElementsService', () => {
  let service: RelatedElementsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelatedElementsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
