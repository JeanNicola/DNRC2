import { CdkStepper, StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatStepper } from '@angular/material/stepper';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { DataSourceTypes } from '../../../constants/DataSourceTypes';
import { UsgsQuadValuesService } from '../../../usgs-quad-map-table/components/insert-usgs-quad-map/services/usgs-quad-values.service';

@Component({
  selector: 'app-create-data-source-dialog',
  templateUrl: './create-data-source-dialog.component.html',
  styleUrls: ['./create-data-source-dialog.component.scss'],
  providers: [CdkStepper, UsgsQuadValuesService],
})
export class CreateDataSourceDialogComponent extends InsertDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<CreateDataSourceDialogComponent>,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public service: UsgsQuadValuesService
  ) {
    super(dialogRef, data);
  }

  public title = this.data.title;
  public currentSourceType = null;
  public dataSourceTypes = DataSourceTypes;

  // Field Investigation properties
  public fieldInvestigationFormGroup = new FormGroup({});

  // Aerial Photo properties
  public aerialFormGroup = new FormGroup({});

  // Water Survey properties
  public waterSurveyFormGroup = new FormGroup({});

  // USGS Quad Map properties
  @ViewChild('stepper') stepper: MatStepper;
  public formWasInitialized = false;
  public currentStep = 0;
  public selectedStepIndex = 0;

  public usgsSearchForm = new FormGroup({});
  public usgsSearchColumns = this.data.usgsColumns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInSearch
  );

  public usgsSortDirection = 'asc';
  public usgsQueryResult: any;
  public usgsRows: any[] = null;
  public usgsDataFound = true;
  public usgsSortColumn = 'name';
  public usgsQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.usgsSortDirection,
    sortColumn: this.usgsSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  protected usgsPageSizeOptions: number[] = [25, 50, 100];
  public usgsHideActions = true;
  public usgsHideHeader = false;
  public usgsClickableRow = true;
  public usgsDblClickableRow = true;

  public usgsRow;

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    const filterFunction =
      this.mode === DataManagementDialogModes.Update
        ? (field) => field?.displayInEdit ?? true
        : (field) => field?.displayInInsert ?? true;
    return columns.filter(filterFunction).map((item) => ({
      ...item,
    }));
  }

  public onInputChangeHandler($event) {
    if ($event.fieldName === 'sourceType') {
      this.currentSourceType = $event.value;
      if ($event.value !== DataSourceTypes.WATER_RESOURCE_SURVEY) {
        this.waterSurveyFormGroup = new FormGroup({});
      }
      if ($event.value !== DataSourceTypes.AERIAL_PHOTO) {
        this.aerialFormGroup = new FormGroup({});
      }
      if ($event.value !== DataSourceTypes.USGS_QUAD_MAPS) {
        this.selectedStepIndex = 0;
        this.usgsRow = null;
      }
    }
  }

  // USGS Quad Map Methods
  public ngAfterViewInit(): void {
    // Stepper will only be available on INSERT mode

    setTimeout(() => {
      this.formWasInitialized = true;
      if (this.stepper) {
        this.stepper.steps.forEach((step, toGoIndex) => {
          step.select = () => {
            this.handleCurrentStep(toGoIndex);
          };
        });
      }
    });
  }

  public stepping(step: StepperSelectionEvent): void {
    this.selectedStepIndex = step.selectedIndex;
    this.currentStep = step.selectedIndex;
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 1) {
        this.usgsRow = null;
        this.usgsRows = null;
      }

      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.usgsQueryParameters.filters = { ...this.usgsSearchForm.value };
        this.usgsQueryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public handleCurrentStep(toGoIndex: number): void {
    if (this.currentStep === 0 && this.formGroup.valid) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    } else if (this.currentStep !== 0) {
      this.currentStep = toGoIndex;
      setTimeout(() => {
        this.selectedStepIndex = toGoIndex;
      });
    }
  }

  protected postLookup(dataIn: any): any {
    // If only one record is returned, then automatically pick it.
    if (dataIn.currentPage === 1 && dataIn.results.length === 1) {
      this.usgsRow = dataIn.results[0];
      this.save();
    } else {
      return dataIn;
    }
  }

  protected lookup(): void {
    this.service.get(this.usgsQueryParameters).subscribe((data) => {
      this.usgsQueryResult = this.postLookup(data);
      this.usgsRows = data.results;
      this.usgsRow = null;
      this.usgsDataFound = data.totalElements > 0;
    });
  }

  public onRowClick(idx: number): void {
    this.usgsRow = this.usgsRows[idx];
  }

  public onRowDoubleClick(idx: number): void {
    this.usgsRow = this.usgsRows[idx];
    this.save();
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      this.usgsQueryParameters.sortColumn = sort.active.toUpperCase();
      this.usgsQueryParameters.sortDirection = sort.direction.toUpperCase();
      this.lookup();
    }
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      this.usgsQueryParameters.pageSize = pagingOptions.pageSize;
      this.usgsQueryParameters.pageNumber = pagingOptions.pageIndex + 1;
      this.lookup();
    }
  }

  public save(): void {
    const dto = {
      ...this.formGroup.getRawValue(),
    };

    if (this.currentSourceType === DataSourceTypes.FIELD_INVESTIGATION) {
      dto.investigationDate =
        this.fieldInvestigationFormGroup.getRawValue()?.investigationDate;
    }

    if (this.currentSourceType === DataSourceTypes.USGS_QUAD_MAPS) {
      dto.usgs = {
        utmpId: this.usgsRow.utmpId,
      };
    }

    if (
      [DataSourceTypes.WRS_AERIAL_PHOTO, DataSourceTypes.AERIAL_PHOTO].includes(
        this.currentSourceType
      )
    ) {
      dto.aerialPhoto = {
        ...this.aerialFormGroup.getRawValue(),
      };
    }

    if (this.currentSourceType === DataSourceTypes.WATER_RESOURCE_SURVEY) {
      dto.waterResourceSurvey = {
        ...this.waterSurveyFormGroup.getRawValue(),
      };
    }

    this.dialogRef.close(dto);
  }
}
