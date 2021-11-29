import { TestBed } from '@angular/core/testing';

import { IrrigationTypesService } from './irrigation-types.service';

describe('IrrigationTypesService', () => {
  let service: IrrigationTypesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IrrigationTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
