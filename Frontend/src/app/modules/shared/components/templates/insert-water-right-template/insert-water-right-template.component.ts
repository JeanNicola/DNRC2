import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';

import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { BaseDataService } from '../../../services/base-data.service';

@Component({
  selector: 'app-insert-water-right-template',
  templateUrl: './insert-water-right-template.component.html',
  styleUrls: ['./insert-water-right-template.component.scss'],
})
export class InsertWaterRightTemplateComponent extends DataManagementDialogComponent {
  constructor(
    public service: BaseDataService,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertWaterRightTemplateComponent>
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
  public mode = DataManagementDialogModes.Insert;
  public title = this.data.title;
  public dataFound = true;
  public hideActions = true;
  public hideEdit = false;
  public hideDelete = false;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public idArray = [];
  public currentStep = 0;
  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'desc',
    sortColumn: this._hasColumn('completeWaterRightNumber')
      ? 'completeWaterRightNumber'
      : 'waterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  // Water Right Info

  public waterRightsQueryResult: any;
  public waterRights: any[];
  public waterRightsSearchForm = new FormGroup({});
  public checkboxesForm: FormGroup = new FormGroup({});
  public checkedWaterRights = {};
  public selectedWaterRightsCount = 0;

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    this.dataFound = true;
    this.service
      .get(this.queryParameters, ...this.idArray)
      .subscribe((data) => {
        const localData = this.postLookup(data);

        if (this.currentStep === 1) {
          this.waterRightsQueryResult = localData;
          this.waterRights = localData.results;
          this.dataFound = this.waterRights.length > 0;
        }
      });
  }

  protected _onRowStateChangedHandler(idx) {}

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
        this.selectedWaterRightsCount = 0;
        this.checkedWaterRights = {};
        this.queryParameters.filters = { ...this.waterRightsSearchForm.value };
        this.queryParameters.pageNumber = 1;

        this.lookup();
      }

      this.stepper.selected.completed = true;
    }
  }

  public onRowDoubleClick(idx: number): void {
    if (this.currentStep === 1) {
      this.dialogRef.close([this.waterRights[idx]]);
    }
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.queryParameters.sortColumn = sort.active;
      this.queryParameters.sortDirection = sort.direction;
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
      this.dialogRef.close(
        Object.values(this.checkedWaterRights).map((formGroup: FormGroup) =>
          formGroup.getRawValue()
        )
      );
    }
  }

  private _hasColumn(columnId: string): boolean {
    for (let i = 0; i < this.displayFields.length; i++) {
      if (this.displayFields[i].columnId === columnId) {
        return true;
      }
    }
    return false;
  }
}
