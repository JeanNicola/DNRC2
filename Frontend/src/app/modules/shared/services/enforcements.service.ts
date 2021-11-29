import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

export interface Enforcement {
  name: string;
  areaId: string;
}

@Injectable()
export class EnforcementsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/enforcements'];
  }
}
