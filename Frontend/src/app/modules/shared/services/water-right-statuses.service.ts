import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

export interface WaterRightStatus {
  value: string;
  description: string;
}

@Injectable()
export class WaterRightStatusesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/water-right-types', '/water-right-statuses/all'];
  }
}
