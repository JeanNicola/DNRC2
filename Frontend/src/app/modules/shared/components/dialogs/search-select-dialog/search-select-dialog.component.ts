import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { DataQueryParametersInterface } from '../../../interfaces/data-query-parameters.interface';
import { BaseDataService } from '../../../services/base-data.service';
import { DataManagementDialogComponent } from '../data-management/data-management-dialog.component';
import { DataManagementDialogModes } from '../data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from '../data-management/data-management-dialog.interface';

@Component({
  selector: 'app-search-select-dialog',
  templateUrl: './search-select-dialog.component.html',
  styleUrls: ['./search-select-dialog.component.scss'],
})
export class SearchSelectDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<SearchSelectDialogComponent>,
    public service: BaseDataService
  ) {
    super(dialogRef);
  }

  public mode = DataManagementDialogModes.Insert;
  public title = '';
  public searchTitle = '';
  public selectTitle = '';
  public addTooltip = '';
  public searchColumns = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInSearch
  );
  public displayFields = this.data.columns;
  public sortColumn = null;
  public sortDirection = 'asc';
  public queryResult: any;
  public rows: any[];
  public dataFound = true;

  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: this.sortDirection,
    sortColumn: '',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  public hideActions = true;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;

  public row;

  @ViewChild('stepper') stepper: MatStepper;

  public initFunction(): void {
    this.queryParameters = {
      sortDirection: this.sortDirection,
      sortColumn: this.sortColumn || this.displayFields[0].columnId,
      pageSize: 25,
      pageNumber: 1,
      filters: {},
    };
  }

  protected lookup(): void {
    this.service.get(this.queryParameters).subscribe((data) => {
      this.queryResult = this.postLookup(data);

      this.rows = data.results;
      this.row = null;
      this.dataFound = data.totalElements > 0;
    });
  }

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  public stepping(step: StepperSelectionEvent): void {
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 0) {
        this.rows = [];
      }

      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.queryParameters.filters = { ...this.formGroup.value };
        this.queryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active.toUpperCase();
      this.queryParameters.sortDirection = sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.queryParameters.pageSize = pagingOptions.pageSize;
      this.queryParameters.pageNumber = pagingOptions.pageIndex + 1;
      this.lookup();
    }
  }

  public onRowClick(idx: number): void {
    this.row = this.rows[idx];
  }

  // exit dialog on double click
  public onRowDoubleClick(idx: number): void {
    this.dialogRef.close(this.rows[idx]);
  }

  public save(): void {
    this.dialogRef.close(this.row);
  }
}
