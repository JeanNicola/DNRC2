import { TestBed } from '@angular/core/testing';

import { FullTextService } from './full-text.service';

describe('FullTextService', () => {
  let service: FullTextService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FullTextService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
