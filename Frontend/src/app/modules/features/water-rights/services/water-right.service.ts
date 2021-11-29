import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class WaterRightService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/water-rights'];
  }

  public get(
    queryParameters: any,
    ...ids: string[]
  ): Observable<DataPageInterface<any>> {
    const query: HttpParams = this.buildQueryString(queryParameters);
    return this.http.get<DataPageInterface<any>>(this.buildUrl(...ids), {
      params: query,
      responseType: 'json',
    });
  }
}
