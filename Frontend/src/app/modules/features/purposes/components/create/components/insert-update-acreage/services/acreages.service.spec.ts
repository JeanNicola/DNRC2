import { TestBed } from '@angular/core/testing';

import { AcreagesService } from './acreages.service';

describe('AcreagesService', () => {
  let service: AcreagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AcreagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
