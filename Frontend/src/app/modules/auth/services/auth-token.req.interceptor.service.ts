import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpHeaders,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { EMPTY, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { SnackBarService } from '../../core/services/snack-bar/snack-bar.service';
import { dateTimeDisplayFormat } from '../../shared/constants/date-time-formats';
import * as moment from 'moment';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthTokenRequestInterceptor implements HttpInterceptor {
  private EXPIRATION_OFFSET = 3600000;
  private requests = new Map();
  // List of URLs where common error handling and token refresh is not being performed
  private exceptions: string[] = ['login', 'update-token'];
  private errorOccurred = false;
  private renewingToken = false;

  public constructor(
    private snackBarService: SnackBarService,
    private authService: AuthService
  ) {}

  public intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const headers = req.headers;
    headers.append('Cache-Control', 'no-cache');
    headers.append('Pragma', 'no-cache');

    const token: string = this.authService.getToken();
    const expiration: moment.Moment = moment(
      this.authService.getExpirationDate()
    );

    // Check to see if any parameters are missing -> typically identified by var=undefined
    if (req.urlWithParams.indexOf('=undefined') !== -1) {
      console.error('Potential missing data in API call:', req.urlWithParams);
      throw new Error('Missing data');
    }

    let newReq: HttpRequest<any>;

    if (!token) {
      newReq = req.clone({
        headers,
      });
    } else {
      newReq = req.clone({
        headers,
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }

    // Check expiration date
    if (
      expiration.isValid() &&
      this.exceptions.every((term: string) => newReq.url.indexOf(term) === -1)
    ) {
      // If the current time is within the EXPIRATION_OFFSET of expiration and the token is currently
      // not being renewed, renew it
      if (
        moment(expiration)
          .subtract(this.EXPIRATION_OFFSET)
          .isBefore(moment()) &&
        !this.renewingToken
      ) {
        this.renewingToken = true;
        this.authService.updateToken().subscribe((data) => {
          this.renewingToken = false;
          console.log('Session extended');
        });
      }
    }

    // Log the REST calls
    this.requests.set(newReq.url, {
      type: newReq.method,
      start: moment(),
    } as Timing);

    return next.handle(newReq).pipe(
      map((resp: HttpResponse<any>) => {
        if (resp.url) {
          this._printRequestTime(resp.url);
          this.errorOccurred = false;
        }
        return resp;
      }),
      catchError((err: HttpErrorResponse) => {
        this._printRequestTime(err.url);
        // If URL request is in exception list, ignore "global" error handling, return to the login screen and just pass the error back up
        if (
          this.exceptions.some(
            (term: string) => newReq.url.indexOf(term) !== -1
          )
        ) {
          return throwError(err);
        }

        /*
         * Perform global error handling. Stgatus codes:
         * - 0: this is an internal HTTP error, nto from a server
         * - 401 No Authorized: tell the use then send them to the login screen
         * - 403 Forbidden - user cannot peform the requested action
         * - 500+: internal backend service error
         */

        let message: string;
        switch (true) {
          case err.status === 0:
            message =
              'Cannot contact backend services. See browser console for additional information.';
            // Only show error messages once.
            if (!this.errorOccurred) {
              // Set the flag to only show one error message
              this.snackBarService.open(message, 'Dismiss', 0);
            }
            break;
          case err.status === 401:
            // Since this redirects to the login screen, nothing to return up the stack
            message = 'Your session has expired. Please re-login.';
            // Only show error messages once.
            if (!this.errorOccurred) {
              // Set the flag to only show one error message
              this.snackBarService.open(message);
            }
            setTimeout(() => {
              this.authService.logout();
            }, 5000);
            return EMPTY;
            break;
          case err.status === 403:
            // Only show error messages once.
            if (!this.errorOccurred) {
              // Set the flag to only show one error message

              message = 'You do not have permissions to perform this action.';
              this.snackBarService.open(message);
            }
            console.error(err);
            break;
          case err.status >= 500:
            // Only show error messages once.
            if (!this.errorOccurred) {
              // Set the flag to only show one error message

              message =
                // eslint-disable-next-line max-len
                'The backend services are experiencing problems at this time. Please check the browser console for additional information.';
              this.snackBarService.open(message, 'Dismiss', 0);
            }
            console.error(err);
        }

        this.errorOccurred = true;
        return throwError(err);
      })
    );
  }

  // Print the duration of the URL request in seconds
  private _printRequestTime(url: string): void {
    // Get the URL without any query parameters
    // Sometimes this can be called without a url... not sure why.
    if (url) {
      const respUrl = url.split('?')[0];

      // Make sure the requests Map has values and the URL is actually found in the Map
      // then print timing calculations
      if (this.requests.size > 0) {
        const timing: Timing = this.requests.get(respUrl) as Timing;
        if (timing && !environment.production) {
          console.log(
            `${timing.type} : ${respUrl} : ${moment().diff(
              timing.start,
              'seconds',
              true
            )}  secs`
          );
          // Remove the request after printing
          this.requests.delete(respUrl);
        }
      }
    }
  }
}

export interface Timing {
  type: string;
  start: moment.Moment;
}
