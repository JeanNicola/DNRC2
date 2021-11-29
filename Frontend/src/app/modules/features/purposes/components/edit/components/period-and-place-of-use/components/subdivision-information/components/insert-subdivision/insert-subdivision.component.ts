import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { SubdivisionCodesService } from 'src/app/modules/shared/services/subdivision-codes.service';

@Component({
  selector: 'app-insert-subdivision',
  templateUrl: './insert-subdivision.component.html',
  styleUrls: ['insert-subdivision.component.scss'],
  providers: [SubdivisionCodesService],
})
export class InsertSubdivisionComponent extends InsertDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<InsertSubdivisionComponent>,
    public service: SubdivisionCodesService
  ) {
    super(dialogRef, data);
  }

  @ViewChild('stepper') stepper: MatStepper;

  public mode = DataManagementDialogModes.Insert;
  public searchColumns = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInSearch
  );
  public formColumns = this.data.columns.filter((item) =>
    item?.displayInInsert == null ? true : item?.displayInInsert
  );

  public sortDirection = 'asc';
  public queryResult: any;
  public rows: any[];
  public dataFound = true;

  public title = 'Add New Subdivision';
  public sortColumn = 'dnrcName';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: this.sortDirection,
    sortColumn: 'dnrcName',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  protected pageSizeOptions: number[] = [25, 50, 100];
  public hideActions = true;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;

  public row;

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.queryParameters.filters.countyId = this.data.values.countyId;
    this.service.get(this.queryParameters).subscribe((data) => {
      this.queryResult = this.postLookup(data);
      this.rows = data.results;
      this.row = null;
      this.dataFound = data.totalElements > 0;
    });
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
        this.formGroup.get('lot').setValue(null);
        this.formGroup.get('blk').setValue(null);
        this.queryParameters.filters = { ...this.formGroup.value };
        this.queryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public onRowClick(idx: number): void {
    this.row = this.rows[idx];
  }

  public onRowDoubleClick(idx: number): void {
    this.stepper.next();
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

  public save(): void {
    this.dialogRef.close({
      lot: this.formGroup.getRawValue().lot,
      blk: this.formGroup.getRawValue().blk,
      code: this.row.code,
    });
  }
}
