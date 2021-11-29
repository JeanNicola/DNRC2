import { TestBed } from '@angular/core/testing';

import { ObjectorsVersionService } from './objectors-version.service';

describe('ObjectorsVersionService', () => {
  let service: ObjectorsVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ObjectorsVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
