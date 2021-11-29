import { TestBed } from '@angular/core/testing';

import { NoticeOtherPartiesService } from './notice-other-parties.service';

describe('NoticeOtherPartiesService', () => {
  let service: NoticeOtherPartiesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NoticeOtherPartiesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
