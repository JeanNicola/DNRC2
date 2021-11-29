import { TestBed } from '@angular/core/testing';

import { ContactPhoneEmailService } from './contact-phone-email.service';

describe('ContactPhoneEmailService', () => {
  let service: ContactPhoneEmailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactPhoneEmailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
