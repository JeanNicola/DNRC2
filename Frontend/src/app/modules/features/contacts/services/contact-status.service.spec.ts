import { TestBed } from '@angular/core/testing';

import { ContactStatusService } from './contact-status.service';

describe('ContactStatusService', () => {
  let service: ContactStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
