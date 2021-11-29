import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SearchService } from 'src/app/modules/shared/services/search.service';

@Injectable()
export class SearchApplicantService extends SearchService {
  constructor(protected http: HttpClient) {
    super(http);
    this.urlList = ['/applications/view/owner'];
  }
}
