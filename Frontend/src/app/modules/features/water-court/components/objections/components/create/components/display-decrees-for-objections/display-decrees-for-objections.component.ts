import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { DecreeBasinsService } from '../../../../../cases/components/create/services/decree-basins.service';

@Component({
  selector: 'app-display-decrees-for-objections',
  templateUrl: './display-decrees-for-objections.component.html',
  styleUrls: ['./display-decrees-for-objections.component.scss'],
  providers: [DecreeBasinsService],
})
export class DisplayDecreesForObjectionsComponent implements OnInit {
  @Input() public selectedDecreeRowIdx: number;
  @Input() filters = null;

  @Output() onDecree: EventEmitter<any> = new EventEmitter<any>();
  @Output() clearSelection: EventEmitter<any> = new EventEmitter<any>();

  constructor(public decreeBasinsService: DecreeBasinsService) {}

  public decreeBasinSearchDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dctpCodeDescription',
      title: 'Decree Type',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'issueDate',
      title: 'Decree Issued Date',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public decreeBasinSortDirection = 'asc';
  public decreeBasinQueryResult: any;
  public decreeBasinRows: any[] = null;
  public decreeBasinDataFound = true;
  public decreeBasinSortColumn = 'basin';
  public decreeBasinQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.decreeBasinSortDirection,
    sortColumn: this.decreeBasinSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  protected decreeBasinPageSizeOptions: number[] = [25, 50, 100];
  public decreeBasinHideActions = true;
  public decreeBasinHideHeader = false;
  public decreeBasinClickableRow = true;
  public decreeBasinDblClickableRow = true;

  public ngOnInit(): void {
    this.decreeBasinQueryParameters.filters = this.filters;
    this.decreeBasinQueryParameters.pageNumber = 1;
    this.lookup();
  }

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.decreeBasinsService
      .get(this.decreeBasinQueryParameters)
      .subscribe((data) => {
        this.decreeBasinQueryResult = this.postLookup(data);
        this.decreeBasinRows = data.results;
        this.decreeBasinDataFound = data.totalElements > 0;
      });
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.decreeBasinQueryParameters.sortColumn = sort.active.toUpperCase();
      this.decreeBasinQueryParameters.sortDirection =
        sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onRowClick(idx: number): void {
    this.selectedDecreeRowIdx = idx;
    this.onDecree.emit({ idx, value: { ...this.decreeBasinRows[idx] } });
  }

  public onRowDoubleClick(idx: number): void {
    this.onDecree.emit({ idx, value: { ...this.decreeBasinRows[idx] } });
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.decreeBasinQueryParameters.pageSize = pagingOptions.pageSize;
      this.decreeBasinQueryParameters.pageNumber = pagingOptions.pageIndex + 1;

      this.lookup();
    }
  }
}
