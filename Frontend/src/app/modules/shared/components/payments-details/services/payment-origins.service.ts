import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseDataService } from '../../../services/base-data.service';

@Injectable({
  providedIn: 'root',
})
export class PaymentOriginsService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/references/application-payment-origins'];
  }
}
