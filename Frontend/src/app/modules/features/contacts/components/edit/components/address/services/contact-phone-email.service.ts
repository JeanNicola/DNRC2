import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class ContactPhoneEmailService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/customers', '/electronic-contacts'];
  }

  public getElectronicContactTypes(): Observable<any> {
    return this.http.get<any>(
      `${this.baseURL}/references/electronic-contacts/all`,
      { responseType: 'json' }
    );
  }
}
