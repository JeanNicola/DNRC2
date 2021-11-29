import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable({
  providedIn: 'root',
})
export class AffectedChangeApplicationsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/ownership-updates', '/applications'];
  }

  public deleteAll(...ids: string[]): Observable<any> {
    return this.http
      .delete(this.buildUrl(...ids) + '/all', {
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
