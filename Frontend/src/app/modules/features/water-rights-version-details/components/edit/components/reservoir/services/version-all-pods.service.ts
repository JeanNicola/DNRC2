import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class VersionAllPodsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/water-rights', '/versions', '/pods/all'];
  }
}

export interface PodOption {
  podId: number;
  podNumber: number;
  legalLandDescriptionId: number;
  countyId: number;
  description320: number;
  description160: number;
  description80: number;
  description40: number;
  governmentLot: number;
  section: number;
  township: number;
  townshipDirection: string;
  range: number;
  rangeDirection: string;
}
