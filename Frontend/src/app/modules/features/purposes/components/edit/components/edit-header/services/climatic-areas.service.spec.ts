import { TestBed } from '@angular/core/testing';

import { ClimaticAreasService } from './climatic-areas.service';

describe('ClimaticAreasService', () => {
  let service: ClimaticAreasService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClimaticAreasService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
