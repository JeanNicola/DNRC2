import { TestBed } from '@angular/core/testing';

import { YesNoValuesService } from './yes-no-values.service';

describe('YesNoValuesService', () => {
  let service: YesNoValuesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(YesNoValuesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
