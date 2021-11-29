import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { DataPageInterface } from '../interfaces/data-page.interface';
import { DataQueryParametersInterface } from '../interfaces/data-query-parameters.interface';
import { flattenObject } from 'src/app/modules/shared/utilities/flatten-object';
import { environment } from 'src/environments/environment';
import * as moment from 'moment';
import { catchError } from 'rxjs/operators';

@Injectable()
export class BaseDataService {
  protected urlList: string[];
  protected baseURL = `${environment.api.baseUrl}${environment.api.version}`;
  protected endpoint: string;

  constructor(protected http: HttpClient) {}

  get url(): string {
    // specifically for the endpoint service
    return this.urlList.join('/?');
  }

  /*
   * GET HTTP Request using any available query parameters
   *
   * Input: (Optional) QueryParameters structure
   */
  public get(
    paramsOrId?: DataQueryParametersInterface | string,
    ...ids: string[]
  ): Observable<DataPageInterface<any>> {
    const firstId = typeof paramsOrId === 'string' ? paramsOrId : undefined;

    const query =
      typeof paramsOrId === 'object'
        ? this.buildQueryString(paramsOrId)
        : undefined;

    return this.http.get<DataPageInterface<any>>(
      this.buildUrl(...(firstId ? [firstId, ...ids] : ids)),
      {
        params: query,
        responseType: 'json',
      }
    );
  }

  /*
   * GET HTTP Request to return a list of all data
   */
  public getAll(...ids: string[]): Observable<any> {
    return this.http.get<any>(`${this.buildUrl(...ids)}/all`, {
      responseType: 'json',
    });
  }

  /*
   * POST HTTP Request
   *
   * Input: JSON data structure for the specific REST API
   */
  public insert(newRow: any, ...ids: string[]): Observable<any> {
    return this.http.post<DataPageInterface<any>>(
      this.buildUrl(...ids),
      this.cleanDto(newRow),
      {
        responseType: 'json',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  }

  public uploadFiles(files: any, ...ids: string[]): Observable<any> {
    return this.http.post(this.buildUrl(...ids), this.buildFormFileData(files));
  }

  private buildFormFileData(files: object): FormData {
    if (files == null) {
      return null;
    }
    const formData = new FormData();
    for (const key of Object.keys(files)) {
      if (files[key] instanceof File) {
        formData.append(key, files[key], files[key].name);
      }
    }
    return formData;
  }

  /*
   * DELETE HTTP Request
   *
   * Input: Unique ID of the record to delete
   */
  public delete(...ids: string[]): Observable<any> {
    return this.http
      .delete(this.buildUrl(...ids), {
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

  /*
   * PUT HTTP Request
   *
   * Input: ID of the record to update and JSON data structure for the specific REST API
   */
  public update(updateRow: any, ...ids: any): Observable<any> {
    return this.http.put<any>(this.buildUrl(...ids), this.cleanDto(updateRow), {
      responseType: 'json',
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  protected cleanDto(obj: object): object {
    if (obj == null) {
      return null;
    }
    for (const key of Object.keys(obj)) {
      if (obj[key] === '') {
        obj[key] = null;
      } else if (typeof obj[key] === 'string') {
        obj[key] = (obj[key] as string).toUpperCase();
      } else if (moment.isMoment(obj[key])) {
        // When the HTTP service converts the Moment into JSON,
        // it converts the datetime using UTC. This causes problems on the backend.
        // This code removes any timezone information from the datetime
        obj[key] = moment(obj[key]).utc(true).format();
      }
    }
    return obj;
  }

  protected buildUrl(...ids: any[]): string {
    const newUrlList = [...this.urlList];
    if (
      ids.length === this.urlList.length - 2 &&
      this.urlList[this.urlList.length - 1].length === 0
    ) {
      newUrlList.pop();
    }
    if (ids.length < newUrlList.length - 1) {
      // eslint-disable-next-line @typescript-eslint/quotes
      throw new Error("Wrong number of ids for this service's url");
    }

    const urlPairs: string[] = newUrlList.map((path, i) => {
      const encodedId = i < ids.length ? '/' + encodeURIComponent(ids[i]) : '';
      return path + encodedId;
    });
    if (ids.length > newUrlList.length) {
      urlPairs.push(
        ...ids
          .slice(newUrlList.length)
          .map((id: any) => '/' + encodeURIComponent(id))
      );
    }

    return `${this.baseURL}${urlPairs.join('')}`;
  }

  /*
   * Takes the query parameter structure and builds query parameters for GETs
   * If any parameters have an empty value, ignore them.
   */
  protected buildQueryString(
    queryParameters: DataQueryParametersInterface
  ): HttpParams {
    const paramObj = flattenObject(queryParameters);
    let params: HttpParams = new HttpParams();
    for (const k in paramObj) {
      if (paramObj[k] !== '' && paramObj[k] !== null) {
        // Force strings that visibly show as uppercase using CSS to be
        // converted to uppercase upon being added as params
        const newParam =
          typeof paramObj[k] === 'string'
            ? (paramObj[k] as string).toUpperCase()
            : paramObj[k];
        params = params.set(k, newParam);
      }
    }
    return params;
  }
}
