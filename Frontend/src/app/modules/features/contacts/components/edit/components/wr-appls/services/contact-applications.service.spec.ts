import { TestBed } from '@angular/core/testing';

import { ContactApplicationsService } from './contact-applications.service';

describe('ContactApplicationsService', () => {
  let service: ContactApplicationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactApplicationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
