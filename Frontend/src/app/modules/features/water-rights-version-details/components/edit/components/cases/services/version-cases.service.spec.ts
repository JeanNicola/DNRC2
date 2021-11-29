import { TestBed } from '@angular/core/testing';

import { VersionCasesService } from './version-cases.service';

describe('VersionCasesService', () => {
  let service: VersionCasesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VersionCasesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
