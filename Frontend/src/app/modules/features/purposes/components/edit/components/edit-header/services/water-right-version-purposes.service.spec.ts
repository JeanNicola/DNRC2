import { TestBed } from '@angular/core/testing';

import { WaterRightVersionPurposesService } from './water-right-version-purposes.service';

describe('WaterRightVersionPurposesService', () => {
  let service: WaterRightVersionPurposesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WaterRightVersionPurposesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
