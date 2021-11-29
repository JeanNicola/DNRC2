import { TestBed } from '@angular/core/testing';

import { ContactWaterRightsService } from './contact-water-rights.service';

describe('ContactWaterRightsService', () => {
  let service: ContactWaterRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactWaterRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
