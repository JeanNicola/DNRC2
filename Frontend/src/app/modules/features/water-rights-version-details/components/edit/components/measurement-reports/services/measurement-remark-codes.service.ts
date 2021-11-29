import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

export interface RemarkCode {
  description: string;
  value: string;
  createable: boolean;
}
@Injectable()
export class MeasurementRemarkCodesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/references/measurement-remark-codes'];
  }
}
