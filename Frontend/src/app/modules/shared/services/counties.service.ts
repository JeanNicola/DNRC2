import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

export interface County {
  id: number;
  name: string;
  stateCode: string;
}

@Injectable()
export class CountiesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/counties'];
  }
}
