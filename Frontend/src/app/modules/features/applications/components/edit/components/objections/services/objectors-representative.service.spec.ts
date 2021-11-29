import { TestBed } from '@angular/core/testing';

import { ObjectorsReprsentativeService } from './objectors-reprsentative.service';

describe('ObjectorsReprsentativeService', () => {
  let service: ObjectorsReprsentativeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ObjectorsReprsentativeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
