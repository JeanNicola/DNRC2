import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class PaymentsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/applications', '/payments'];
  }

  autoComplete(applicationId: number): Observable<any> {
    return this.http.post<{ waterRightId: string }>(
      `${this.baseURL}/applications/${applicationId}/auto-complete`,
      {}
    );
  }
}
