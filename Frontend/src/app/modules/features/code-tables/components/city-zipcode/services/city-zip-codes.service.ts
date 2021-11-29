import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { StateCodesPageInterface } from 'src/app/modules/shared/interfaces/state-codes-page.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class CityZipCodesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/zip-codes'];
  }

  public getStateCodes(): Observable<StateCodesPageInterface> {
    const queryParameters: DataQueryParametersInterface = {
      pageSize: 100,
      pageNumber: 1,
      sortColumn: 'CODE',
      sortDirection: 'ASC',
      filters: {},
    };
    const query: HttpParams = this.buildQueryString(queryParameters);
    return this.http.get<StateCodesPageInterface>(
      `${this.baseURL}/state-codes/all`,
      {
        params: query,
        responseType: 'json',
      }
    );
  }

  public deleteCity(id: number): Observable<any> {
    return this.http
      .delete(`${this.baseURL}/cities/${encodeURIComponent(id)}`, {
        responseType: 'json',
      })
      .pipe(
        // treat 404 deletes the same as a successful delete
        catchError((err: HttpErrorResponse) => {
          if (err.status === 404) {
            return of(undefined); // an observable with a single success
          }
          return throwError(err);
        })
      );
  }
}
