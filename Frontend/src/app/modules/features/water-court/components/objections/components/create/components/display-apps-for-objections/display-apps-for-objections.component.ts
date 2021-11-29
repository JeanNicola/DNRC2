import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ElegibleAppsForObjectionsService } from './services/elegible-apps-for-objections.service';

@Component({
  selector: 'app-display-apps-for-objections',
  templateUrl: './display-apps-for-objections.component.html',
  styleUrls: ['./display-apps-for-objections.component.scss'],
  providers: [ElegibleAppsForObjectionsService],
})
export class DisplayAppsForObjectionsComponent implements OnInit {
  constructor(
    public elegibleAppsForObjectionsService: ElegibleAppsForObjectionsService
  ) {}

  @Input() public selectedAppRowIdx: number;
  @Input() filters = null;
  @Output() onApplication: EventEmitter<any> = new EventEmitter<any>();

  public appsDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationTypeDescription',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public appSortDirection = 'asc';
  public appQueryResult: any;
  public appRows: any[] = null;
  public appDataFound = true;
  public appSortColumn = 'applicationId';
  public appQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.appSortDirection,
    sortColumn: this.appSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  protected appPageSizeOptions: number[] = [25, 50, 100];
  public appHideActions = true;
  public appHideHeader = false;
  public appClickableRow = true;
  public appDblClickableRow = true;

  public ngOnInit(): void {
    this.appQueryParameters.filters = this.filters;
    this.appQueryParameters.pageNumber = 1;
    this.lookup();
  }

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.elegibleAppsForObjectionsService
      .get(this.appQueryParameters)
      .subscribe((data) => {
        this.appQueryResult = this.postLookup(data);
        this.appRows = data.results;
        this.appDataFound = data.totalElements > 0;
      });
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.appQueryParameters.sortColumn = sort.active.toUpperCase();
      this.appQueryParameters.sortDirection = sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onRowClick(idx: number): void {
    this.selectedAppRowIdx = idx;
    this.onApplication.emit({ idx, value: { ...this.appRows[idx] } });
  }

  public onRowDoubleClick(idx: number): void {
    this.onApplication.emit({ idx, value: { ...this.appRows[idx] } });
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.appQueryParameters.pageSize = pagingOptions.pageSize;
      this.appQueryParameters.pageNumber = pagingOptions.pageIndex + 1;

      this.lookup();
    }
  }
}
