import { TestBed } from '@angular/core/testing';

import { NoticeWaterRightsService } from './notice-water-rights.service';

describe('NoticeWaterRightsService', () => {
  let service: NoticeWaterRightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NoticeWaterRightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
