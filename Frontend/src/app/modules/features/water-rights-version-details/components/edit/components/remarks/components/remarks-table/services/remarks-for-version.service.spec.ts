import { TestBed } from '@angular/core/testing';

import { RemarksForVersionService } from './remarks-for-version.service';

describe('RemarksForVersionService', () => {
  let service: RemarksForVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RemarksForVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
