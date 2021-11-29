import { TestBed } from '@angular/core/testing';

import { ContractForDeedValuesService } from './contract-for-deed-values.service';

describe('ContractForDeedValuesService', () => {
  let service: ContractForDeedValuesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContractForDeedValuesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
