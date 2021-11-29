import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BaseDataService } from './base-data.service';

@Injectable()
export class RelatedWaterRightsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/related-rights', '/water-rights'];
  }

  get deleteUrl(): string {
    // specifically for the endpoint service
    return this.urlList.join('/?') + '/?';
  }

  public delete(...ids: string[]): Observable<any> {
    return this.http
      .delete(this.buildUrl(...ids.slice(0, 2)) + `/${ids[2]}`, {
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
