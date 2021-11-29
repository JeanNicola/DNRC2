import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DataPageInterface } from 'src/app/modules/shared/interfaces/data-page.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class PodService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/water-rights', '/versions', '/pods'];
  }

  /*
   * Sends up the POD id to copy
   */
  public copyPod(podIdIn: number, ...ids: string[]): Observable<any> {
    return this.http.post<DataPageInterface<any>>(
      `${this.buildUrl(...ids)}/${podIdIn}/copy`,
      this.cleanDto({ podId: podIdIn }),
      {
        responseType: 'json',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  }
}
