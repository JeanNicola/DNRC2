import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class RelatedWaterRightVersionsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/related-rights', '/water-rights-versions'];
  }

  public getAllWaterRights() {
    this.urlList = ['/related-rights/water-rights-versions'];
  }
}
