import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Resolve,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { SnackBarService } from '../snack-bar/snack-bar.service';

type EndPoints = { [key: string]: string[] };

@Injectable({
  providedIn: 'root',
})
export class EndpointsService implements Resolve<boolean> {
  private url = `${environment.api.baseUrl}${environment.api.version}/endpoints`;
  private _endpoints: EndPoints = {};
  private _loadingState: PermissionState = PermissionState.LOADING;

  constructor(
    private http: HttpClient,
    private snackBarService: SnackBarService
  ) {
    // load the endpoints for security
    this.loadEndpoints();
  }

  // This is here so service can be used in routing guard to ensure data is loaded before
  // components using the service are started
  private _loading = new Subject<boolean>();
  public resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    return (
      this._loadingState === PermissionState.LOADED ||
      this._loading.asObservable().pipe(catchError((err) => of(false)))
    );
  }

  public loadEndpoints(): void {
    this.http.get<EndPoints>(`${this.url}`).subscribe(
      (data) => {
        for (const k in data) {
          if (Object.prototype.hasOwnProperty.call(data, k)) {
            const url = this.cleanURL(k);
            this._endpoints[url] = data[k];
          }
        }

        this._loadingState = PermissionState.LOADED;
        this._loading.next(true);
        this._loading.complete();
      },
      (err: HttpErrorResponse) => {
        this._loadingState = PermissionState.ERROR;
        let errMsg = '';
        if (err.status === 0) {
          errMsg =
            ' The system is experiencing issues. Your permissions cannot be loaded. ' +
            'Please open a ticket with the Help Desk to get this resolved.\n More information: ' +
            err.message;
        } else {
          errMsg = err.error.userMessage
            ? err.error.userMessage
            : 'Unknown error';
        }
        this.snackBarService.open(errMsg, 'Dismiss', 0);
        this._loading.error(false);
      }
    );
  }

  // Can be used for debugging
  public getEndpoints(url?: string): EndPoints {
    let endPoints: EndPoints = {};
    if (url) {
      if (url in this._endpoints) {
        url = this.cleanURL(url);
        endPoints[url] = this._endpoints[url];
      } else {
        throw new Error(`EndpointsService.getEndpoints - invalid url ${url}`);
      }
    } else {
      endPoints = this._endpoints;
    }
    return endPoints;
  }

  public canGET(url: string): boolean {
    url = this.cleanURL(url);
    return url in this._endpoints && this._endpoints[url].includes('GET');
  }

  public canPOST(url: string, numberOfIds: number = 0): boolean {
    url = this.cleanURL(url);
    url += '/?'.repeat(numberOfIds);
    return url in this._endpoints && this._endpoints[url].includes('POST');
  }

  public canDELETE(url: string, numberOfIds: number = 1): boolean {
    url = this.cleanURL(url);
    url += '/?'.repeat(numberOfIds);
    return url in this._endpoints && this._endpoints[url].includes('DELETE');
  }

  public canPUT(url: string, numberOfIds: number = 1): boolean {
    url = this.cleanURL(url);
    url += '/?'.repeat(numberOfIds);
    return url in this._endpoints && this._endpoints[url].includes('PUT');
  }

  private cleanURL(url: string): string {
    return url.replace(/\{.*?\}/g, '?');
  }
}

export enum PermissionState {
  LOADING,
  LOADED,
  ERROR,
}
