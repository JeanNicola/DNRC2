import { TestBed } from '@angular/core/testing';

import { CardinalDirectionsService } from './cardinal-directions.service';

describe('CardinalDirectionsService', () => {
  let service: CardinalDirectionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardinalDirectionsService);
  });

  it('should be created', () => {
    void expect(service).toBeTruthy();
  });
});
