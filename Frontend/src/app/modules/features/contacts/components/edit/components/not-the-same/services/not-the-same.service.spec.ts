import { TestBed } from '@angular/core/testing';

import { NotTheSameService } from './not-the-same.service';

describe('NotTheSameService', () => {
  let service: NotTheSameService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotTheSameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
