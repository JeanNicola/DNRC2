import { TestBed } from '@angular/core/testing';

import { ObjectionsVersionService } from './objections-version.service';

describe('ObjectionsVersionServiceService', () => {
  let service: ObjectionsVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ObjectionsVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
