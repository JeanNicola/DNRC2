import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from './base-data.service';

@Injectable()
export class YesNoValuesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/references/yes-no'];
  }
}
