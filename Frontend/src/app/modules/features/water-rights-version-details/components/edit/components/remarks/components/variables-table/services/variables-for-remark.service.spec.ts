import { TestBed } from '@angular/core/testing';

import { VariablesForRemarkService } from './variables-for-remark.service';

describe('VariablesForRemarkService', () => {
  let service: VariablesForRemarkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VariablesForRemarkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
