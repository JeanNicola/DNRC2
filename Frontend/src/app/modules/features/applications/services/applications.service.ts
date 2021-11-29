import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SearchService } from 'src/app/modules/shared/services/search.service';

@Injectable()
export class ApplicationsService extends SearchService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/applications'];
  }

  public update(updateRow: any, ...ids: any): Observable<any> {
    const { applicationId, ...data } = updateRow;
    return super.update(data, ...ids);
  }
}
