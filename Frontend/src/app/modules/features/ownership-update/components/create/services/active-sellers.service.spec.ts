import { TestBed } from '@angular/core/testing';

import { ActiveSellersService } from './active-sellers.service';

describe('ActiveSellersService', () => {
  let service: ActiveSellersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActiveSellersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
