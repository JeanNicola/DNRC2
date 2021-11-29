import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class FeeStatusService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/references/application-payment-fee-statuses'];
  }

  getYesAndNo() : Observable<any> {
    return this.http.get<any>(`${this.baseURL}/references/yes-no`, {responseType: 'json'});
  }
}
