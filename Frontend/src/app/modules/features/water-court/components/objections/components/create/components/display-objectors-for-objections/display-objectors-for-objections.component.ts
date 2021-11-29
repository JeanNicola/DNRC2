import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ContactsService } from 'src/app/modules/features/contacts/services/contacts.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';

@Component({
  selector: 'app-display-objectors-for-objections',
  templateUrl: './display-objectors-for-objections.component.html',
  styleUrls: ['./display-objectors-for-objections.component.scss'],
  providers: [ContactsService],
})
export class DisplayObjectorsForObjectionsComponent implements OnInit {
  @Input() public selectedObjectorRowIdx: number;
  @Input() filters = null;
  @Input() basin = null;
  @Output() onObjector: EventEmitter<any> = new EventEmitter<any>();
  @Output() clearSelection: EventEmitter<any> = new EventEmitter<any>();

  constructor(public contactsService: ContactsService) {}

  public objectorsDisplayColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'contactId',
      title: 'Contact ID',
      type: FormFieldTypeEnum.Input,
      editable: false,
    },
    {
      columnId: 'name',
      title: 'Contact Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'contactTypeValue',
      title: 'Contact Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public objectorSortDirection = 'asc';
  public objectorQueryResult: any;
  public objectorRows: any[] = null;
  public objectorDataFound = true;
  public objectorSortColumn = 'name';
  public objectorQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.objectorSortDirection,
    sortColumn: this.objectorSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  protected objectorPageSizeOptions: number[] = [25, 50, 100];
  public objectorHideActions = true;
  public objectorHideHeader = false;
  public objectorClickableRow = true;
  public objectorDblClickableRow = true;

  public ngOnInit(): void {
    this.objectorQueryParameters.filters = this.filters;
    this.objectorQueryParameters.pageNumber = 1;
    this.lookup();
  }

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.contactsService.get(this.objectorQueryParameters).subscribe((data) => {
      this.objectorQueryResult = this.postLookup(data);
      this.objectorRows = data.results;
      this.objectorDataFound = data.totalElements > 0;
    });
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.objectorQueryParameters.sortColumn = sort.active.toUpperCase();
      this.objectorQueryParameters.sortDirection = sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onRowClick(idx: number): void {
    this.selectedObjectorRowIdx = idx;
    this.onObjector.emit({ idx, value: { ...this.objectorRows[idx] } });
  }

  public onRowDoubleClick(idx: number): void {
    this.onObjector.emit({ idx, value: { ...this.objectorRows[idx] } });
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.objectorQueryParameters.pageSize = pagingOptions.pageSize;
      this.objectorQueryParameters.pageNumber = pagingOptions.pageIndex + 1;

      this.lookup();
    }
  }
}
