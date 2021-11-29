import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';

@Component({
  selector: 'app-search-results-dialog',
  templateUrl: './search-results-dialog.component.html',
  styleUrls: [
    '../dialogs/data-management/data-management-dialog.component.scss',
    './search-results-dialog.component.scss',
  ],
})
export class SearchResultsDialogComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title: string;
      searchValues: any;
      searchService: BaseDataService;
      displayColumns: ColumnDefinitionInterface[];
      sortColumn: string;
      sortDirection: string;
      rowFormatFunction: Function;
    }
  ) {
    super(dialogRef);
  }

  public title = this.data.title;
  public sortDirection = this.data.sortDirection || 'asc';
  public queryResult: any;
  public rows: any[] = null;
  public dataFound = true;
  public sortColumn = this.data.sortColumn;
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: this.sortDirection,
    sortColumn: this.sortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: this.data.searchValues,
  };
  protected sizeOptions: number[] = [25, 50, 100];
  public hideActions = true;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;

  public row;

  protected initFunction(): void {
    this.lookup();
  }

  private postLookup(dataIn: any): any {
    return dataIn;
  }

  private lookup(): void {
    this.data.searchService.get(this.queryParameters).subscribe((data) => {
      this.queryResult = this.postLookup(data);
      this.rows = this.data.rowFormatFunction
        ? data.results.map(this.data.rowFormatFunction)
        : data.results;
      this.row = null;
      this.dataFound = data.totalElements > 0;
    });
  }

  public onRowClick(idx: number): void {
    this.row = this.rows[idx];
  }

  public onRowDoubleClick(idx: number): void {
    this.row = this.rows[idx];
    this.save();
  }

  public onSortRequest(sort: Sort): void {
    this.queryParameters.sortColumn = sort.active.toUpperCase();
    this.queryParameters.sortDirection = sort.direction.toUpperCase();
    this.lookup();
  }

  public onPaging(pagingOptions: PageEvent): void {
    this.queryParameters.pageSize = pagingOptions.pageSize;
    this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
    this.lookup();
  }

  public save(): void {
    this.dialogRef.close(this.row);
  }
}
