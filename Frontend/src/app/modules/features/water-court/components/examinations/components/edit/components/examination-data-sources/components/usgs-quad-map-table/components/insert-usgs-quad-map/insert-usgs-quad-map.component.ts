import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { UsgsQuadValuesService } from './services/usgs-quad-values.service';

@Component({
  selector: 'app-insert-usgs-quad-map',
  templateUrl: './insert-usgs-quad-map.component.html',
  styleUrls: ['./insert-usgs-quad-map.component.scss'],
  providers: [UsgsQuadValuesService],
})
export class InsertUsgsQuadMapComponent extends InsertDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<InsertUsgsQuadMapComponent>,
    public service: UsgsQuadValuesService
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

  public title = 'Add New USGS Quad Map';
  public sortColumn = 'name';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: this.sortDirection,
    sortColumn: 'name',
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
    this.service.get(this.queryParameters).subscribe((data) => {
      if (data.totalElements === 1) {
        this.dialogRef.close({
          utmpId: data.results[0].utmpId,
        });
        return;
      }
      this.queryResult = this.postLookup(data);
      this.rows = data.results;
      this.row = null;
      this.dataFound = data.totalElements > 0;
    });
  }

  public stepping(step: StepperSelectionEvent): void {
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 1) {
        this.rows = null;
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

  public onRowClick(idx: number): void {
    this.row = this.rows[idx];
  }

  public onRowDoubleClick(idx: number): void {
    this.row = this.rows[idx];
    this.save();
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
      utmpId: this.row.utmpId,
    });
  }
}
