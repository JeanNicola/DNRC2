import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ElegibleWrForObjectionsService } from './services/elegible-wr-for-objections.service';

@Component({
  selector: 'app-display-wr-for-objections',
  templateUrl: './display-wr-for-objections.component.html',
  styleUrls: ['./display-wr-for-objections.component.scss'],
  providers: [ElegibleWrForObjectionsService],
})
export class DisplayWrForObjectionsComponent implements OnInit {
  @Input() public selectedWaterRightRowIdx: number;
  @Input() filters = null;
  @Input() basin = null;
  @Output() onWaterRight: EventEmitter<any> = new EventEmitter<any>();
  @Output() clearSelection: EventEmitter<any> = new EventEmitter<any>();

  constructor(
    public elegibleWrForObjectionsService: ElegibleWrForObjectionsService
  ) {}

  public waterRightSearchDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'completeVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public waterRightSortDirection = 'asc';
  public waterRightQueryResult: any;
  public waterRightRows: any[] = null;
  public waterRightDataFound = true;
  public waterRightSortColumn = 'basin';
  public waterRightQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.waterRightSortDirection,
    sortColumn: this.waterRightSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  protected waterRightPageSizeOptions: number[] = [25, 50, 100];
  public waterRightHideActions = true;
  public waterRightHideHeader = false;
  public waterRightClickableRow = true;
  public waterRightDblClickableRow = true;

  public ngOnInit(): void {
    this.waterRightQueryParameters.filters = this.filters;
    this.waterRightQueryParameters.pageNumber = 1;
    this.lookup();
  }

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.elegibleWrForObjectionsService
      .get(this.waterRightQueryParameters, this.basin)
      .subscribe((data) => {
        this.waterRightQueryResult = this.postLookup(data);
        this.waterRightRows = data.results;
        this.waterRightDataFound = data.totalElements > 0;
      });
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.waterRightQueryParameters.sortColumn = sort.active.toUpperCase();
      this.waterRightQueryParameters.sortDirection =
        sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onRowClick(idx: number): void {
    this.selectedWaterRightRowIdx = idx;
    this.onWaterRight.emit({ idx, value: { ...this.waterRightRows[idx] } });
  }

  public onRowDoubleClick(idx: number): void {
    this.onWaterRight.emit({ idx, value: { ...this.waterRightRows[idx] } });
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.waterRightQueryParameters.pageSize = pagingOptions.pageSize;
      this.waterRightQueryParameters.pageNumber = pagingOptions.pageIndex + 1;

      this.lookup();
    }
  }
}
