import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CountiesPageInterface } from 'src/app/modules/shared/interfaces/counties-page.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class SubdivisionCodesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/subdivision-codes'];
  }

  public getCounties(): Observable<CountiesPageInterface> {
    const queryParameters: DataQueryParametersInterface = {
      pageSize: 100,
      pageNumber: 1,
      sortColumn: 'ID',
      sortDirection: 'ASC',
      filters: {},
    };
    const query: HttpParams = this.buildQueryString(queryParameters);
    return this.http.get<CountiesPageInterface>(`${this.baseURL}/counties`, {
      params: query,
      responseType: 'json',
    });
  }
}
