import { TestBed } from '@angular/core/testing';

import { ElementsVersionService } from './elements-version.service';

describe('ElementsVersionService', () => {
  let service: ElementsVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ElementsVersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
