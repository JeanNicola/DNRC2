import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from '../../../services/base-data.service';

@Injectable()
export class ApplicantSearchService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/customers'];
  }
}
