import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ActiveApplicationsService } from '../../services/active-applications.service';

@Component({
  selector: 'app-add-application-dialog',
  templateUrl: './add-application-dialog.component.html',
  styleUrls: ['./add-application-dialog.component.scss'],
  providers: [ActiveApplicationsService],
})
export class AddApplicationDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<AddApplicationDialogComponent>,
    public service: ActiveApplicationsService,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }

  // allow moving to second step
  @ViewChild('stepper') stepper: MatStepper;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public currentStep = 0;
  public searchColumns = this.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInInsert
  );
  public mode = DataManagementDialogModes.Insert;
  public title = this.data.title;
  public displayFields = this.columns;

  // Page Options
  protected pageSizeOptions: number[] = [25, 50, 100];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: this._getColumn('applicationId').columnId,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  public queryResult: any;
  public rows: any[];
  public dataFound = true;

  // Table configuration
  public sortColumn = 'applicationId';
  public sortDirection = 'asc';
  public hideActions = true;
  public hideEdit = false;
  public hideDelete = false;
  public hideHeader = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public checkboxesForm: FormGroup = new FormGroup({});
  public checkedApplications = {};
  public selectedApplicationsCount = 0;

  // Work through the stepper
  public stepping(step: StepperSelectionEvent): void {
    this.currentStep = step.selectedIndex;
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      this.stepper.steps.toArray().forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.queryResult = null;
        this.selectedApplicationsCount = 0;
        this.checkedApplications = {};
        this.queryParameters.filters = { ...this.formGroup.value };
        this.queryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  private _getColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.displayFields.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  protected _onRowStateChangedHandler(idx) {
    let row = this.rows[idx];

    let formGroup = (this.checkboxesForm.get('rows') as FormArray).at(idx);
    if (formGroup.get('checked').value) {
      this.checkedApplications[row.applicationId] = formGroup;
      this.selectedApplicationsCount = this.selectedApplicationsCount + 1;
    } else {
      delete this.checkedApplications[row.applicationId];
      this.selectedApplicationsCount = this.selectedApplicationsCount - 1;
    }
  }

  public handleResponseData(data) {
    const localData = data;
    this.queryResult = localData;
    this.rows = localData.results;
    this.dataFound = this.rows.length > 0;
  }

  public handleErrorOnRequest(err: HttpErrorResponse) {
    const errorBody = err.error as ErrorBodyInterface;
    const message = errorBody.userMessage || ErrorMessageEnum.GET;
    this.snackBar.open(message);
  }

  protected lookup(): void {
    this.service
      .get(this.queryParameters, this.data.values.ownershipUpdateId)
      .subscribe((data) => {
        this.handleResponseData(data);
      }, this.handleErrorOnRequest.bind(this));
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

  public onRowDoubleClick(idx: number): void {
    if (this.currentStep === 1) {
      this.dialogRef.close([this.rows[idx]]);
    }
  }

  public save(): void {
    if (this.currentStep === 1) {
      this.dialogRef.close(
        Object.values(this.checkedApplications).map((formGroup: FormGroup) => {
          return formGroup.getRawValue();
        })
      );
    }
  }
}
