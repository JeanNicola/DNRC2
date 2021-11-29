import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class SellersService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/customers/active'];
  }

  public getSellers(
    waterRights: any,
    queryParameters: DataQueryParametersInterface,
    ...ids: string[]
  ): Observable<any> {
    const query: HttpParams = this.buildQueryString(queryParameters);
    return this.http.post<DataPageInterface<any>>(
      this.buildUrl(...ids),
      this.cleanDto(waterRights),
      {
        params: query,
        responseType: 'json',
      }
    );
  }
}
