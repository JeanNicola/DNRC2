import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { WaterRightService } from 'src/app/modules/features/water-rights/services/water-right.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';

@Component({
  selector: 'app-update-original-right',
  templateUrl: './update-original-right.component.html',
  styleUrls: ['./update-original-right.component.scss'],
  providers: [WaterRightService],
})
export class UpdateOriginalRightComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<UpdateOriginalRightComponent>,
    public service: WaterRightService
  ) {
    super(dialogRef);
  }

  // allow moving to second step
  @ViewChild('stepper') stepper: MatStepper;

  searchColumns = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInSearch
  );
  displayFields = this.data.columns.filter((field) =>
    field?.displayInInsert == null ? true : field?.displayInInsert
  );
  mode = DataManagementDialogModes.Insert;
  title = this.data.title;
  public sortColumn = 'waterRightNumber';
  public sortDirection = 'asc';
  public dataFound = true;
  public hideActions = true;
  public hideEdit = false;
  public hideDelete = false;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public currentStep = 0;
  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'waterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  // Water Right Info

  public waterRightsQueryResult: any;
  public waterRights: any[];
  public waterRightsSelected: any;
  public waterRightsSearchForm = new FormGroup({});

  // Work through the stepper
  public stepping(step: StepperSelectionEvent): void {
    this.currentStep = step.selectedIndex;

    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      // Reset stepper
      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.waterRights = null;
        this.waterRightsQueryResult = null;
        this.queryParameters.filters = { ...this.waterRightsSearchForm.value };
        this.queryParameters.pageNumber = 1;

        this.lookup();
      }

      this.stepper.selected.completed = true;
    }
  }

  public onRowClick(idx: number): void {
    if (this.currentStep === 1) {
      this.waterRightsSelected = this.waterRights[idx];
    }
  }

  public onRowDoubleClick(idx: number): void {
    if (this.currentStep === 1) {
      this.waterRightsSelected = this.waterRights[idx];
      this.save();
    }
  }

  // Allows any children to have local processing once data is returned.
  protected postLookup(dataIn: any): any {
    // if only one row is returned, automatically accept the row.
    if (dataIn.results.length === 1) {
      this.dialogRef.close(dataIn.results[0]);
    }
    return dataIn;
  }

  protected lookup(): void {
    this.service
      .get({
        countActiveChangeAuthorizationVersions: true,
        ...this.queryParameters,
      })
      .subscribe((data) => {
        const localData = this.postLookup(data);

        if (this.currentStep === 1) {
          this.waterRightsQueryResult = localData;
          this.waterRights = localData.results;
          this.waterRightsSelected = null;
          this.dataFound = this.waterRights.length > 0;
        }
      });
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
    if (this.currentStep === 1) {
      this.dialogRef.close(this.waterRightsSelected);
    }
  }
}
