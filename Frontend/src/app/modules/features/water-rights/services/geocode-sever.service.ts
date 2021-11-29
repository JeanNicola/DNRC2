import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
@Injectable()
export class SeverGeocodesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/water-rights', '/geocodes/sever'];
  }

  public sever(newRow: any, ...ids: string[]): Observable<any> {
    return this.http
      .post<DataPageInterface<any>>(
        this.buildUrl(...ids),
        this.cleanDto(newRow),
        {
          responseType: 'json',
          headers: {
            'Content-Type': 'application/json',
          },
        }
      )
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
