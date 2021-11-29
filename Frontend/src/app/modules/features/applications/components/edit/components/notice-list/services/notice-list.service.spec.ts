import { TestBed } from '@angular/core/testing';

import { NoticeListService } from './notice-list.service';

describe('NoticeListService', () => {
  let service: NoticeListService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NoticeListService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
