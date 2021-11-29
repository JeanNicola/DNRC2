import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Injectable()
export class CaseAssignmentTypesService extends BaseDataService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/case-assignment-types'];
  }

  public getPrograms(): Observable<any> {
    return this.http.get(
      `${this.baseURL}/references/case-assignment-type-programs`,
      {
        responseType: 'json',
      }
    );
  }
}
