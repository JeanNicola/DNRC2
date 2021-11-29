import { Component, Input, OnInit } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { DataQueryParametersInterface } from '../../../interfaces/data-query-parameters.interface';
import { OfficeService } from './services/office.service';
import { StaffService } from './services/staff.service';

@Component({
  selector: 'app-file-location-processor',
  templateUrl: './file-location-processor.component.html',
  styleUrls: ['./file-location-processor.component.scss'],
  providers: [OfficeService, StaffService],
})
export class FileLocationProcessorComponent implements OnInit {
  @Input() set date(d: string) {
    this.earliestDate = d;
  }

  public earliestDate: string;
  public idArray: string[] = [];
  public officeSubject: ReplaySubject<any>;
  public staffSubject: ReplaySubject<any>;
  constructor(
    public officeService: OfficeService,
    public staffService: StaffService
  ) {}
  ngOnInit(): void {
    this.populateDropdowns();
    this.initFunction();
  }

  protected initFunction(): void {}

  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public populateDropdowns(): void {
    this.officeSubject = new ReplaySubject(1);
    this.officeService
      .get(this.queryParameters)
      .subscribe((offices: { results: Office[] }) => {
        this.officeSubject.next(offices);
        this.officeSubject.complete();
      });
    this.staffSubject = new ReplaySubject(1);
    this.staffService
      .get(this.queryParameters)
      .subscribe((staff: { results: Staff[] }) => {
        this.staffSubject.next(staff);
        this.staffSubject.complete();
      });
  }
}

export interface Office {
  description: string;
  officeId: string;
}

export interface Staff {
  name: string;
  staffId: string;
}
