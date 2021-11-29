import { TestBed } from '@angular/core/testing';

import { ContactAddressesService } from './contact-addresses.service';

describe('ContactAddressesService', () => {
  let service: ContactAddressesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactAddressesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
