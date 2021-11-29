import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from './base-data.service';

@Injectable()
export class RelatedElementsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/related-rights', '/related-elements'];
  }
}
