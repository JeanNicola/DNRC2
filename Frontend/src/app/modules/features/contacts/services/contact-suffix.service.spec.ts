import { TestBed } from '@angular/core/testing';

import { ContactSuffixService } from './contact-suffix.service';

describe('ContactSuffixService', () => {
  let service: ContactSuffixService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactSuffixService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
