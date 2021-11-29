import { TestBed } from '@angular/core/testing';

import { ReportUrlService } from './report-url.service';

describe('ReportUrlService', () => {
  let service: ReportUrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReportUrlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
