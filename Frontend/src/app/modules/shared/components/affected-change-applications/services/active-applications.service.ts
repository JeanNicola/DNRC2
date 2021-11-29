import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class ActiveApplicationsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/ownership-updates', '/applications/active'];
  }
}
