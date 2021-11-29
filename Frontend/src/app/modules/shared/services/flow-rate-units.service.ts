import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

export interface FlowRateUnitType {
  description: string;
  value: string;
}

@Injectable()
export class FlowRateUnitsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/references/flow-rate-units'];
  }
}
