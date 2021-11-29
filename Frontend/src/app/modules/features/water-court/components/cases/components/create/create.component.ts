import { CdkStepper, StepperSelectionEvent } from '@angular/cdk/stepper';
import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
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
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { CasesAndHearingsService } from '../../services/cases-and-hearings.service';
import { CaseProgramTypes } from './enums/caseProgramTypes';
import { DecreeBasinsService } from './services/decree-basins.service';
import { SearchService as SearchApplicationsService } from 'src/app/modules/shared/services/search.service';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss'],
  providers: [
    CdkStepper,
    CasesAndHearingsService,
    DecreeBasinsService,
    SearchApplicationsService,
  ],
})
export class CreateComponent
  extends InsertDialogComponent
  implements AfterViewInit
{
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<CreateComponent>,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public service: CasesAndHearingsService,
    public searchApplicationsService: SearchApplicationsService,
    public decreeBasinsService: DecreeBasinsService
  ) {
    super(dialogRef, data);
  }

  @ViewChild('stepper') stepper: MatStepper;

  public title = this.data.title;
  public tooltip = 'Create';

  public programTypes = CaseProgramTypes;
  public displayFields = this._getDisplayFields(this.data.createColumns);
  public currentProgramType = null;
  public currentCaseType = null;

  public formWasInitialized = false;
  public currentStep = 0;
  public selectedStepIndex = 0;

  // Decree Basin Search properties
  public decreeBasinSearchForm = new FormGroup({});

  protected initFunction() {
    if (this.data.attachedApplicationId) {
      this.currentProgramType = CaseProgramTypes.NA_PROGRAM;
    } else {
      this.currentProgramType = this.data?.values?.programType ?? null;
    }

    this.currentCaseType = this.data?.values?.caseType ?? null;
    if (this.currentCaseType) {
      this.formGroup.markAsDirty();
    }
  }

  public selectedRow: number;
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

  public decreeBasinRow;

  public decreeBasinSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'basin',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];

  public decreeBasinSearchDisplayColumns = [
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

  // Application Search properties
  public applicationSearchForm = new FormGroup({});

  public applicationSortDirection = 'asc';
  public applicationQueryResult: any;
  public applicationRows: any[] = null;
  public applicationDataFound = true;
  public applicationSortColumn = 'basin';
  public applicationQueryParameters: DataQueryParametersInterface = {
    sortDirection: this.applicationSortDirection,
    sortColumn: this.applicationSortColumn,
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };
  protected applicationPageSizeOptions: number[] = [25, 50, 100];
  public applicationHideActions = true;
  public applicationHideHeader = false;
  public applicationClickableRow = true;
  public applicationDblClickableRow = true;

  public applicationSearchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
    },
  ];

  public applicationSearchDisplayColumns = [
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
    {
      columnId: 'applicationTypeDescription',
      title: 'Appl. Type',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public applicationRow;

  protected postLookup(dataIn: any): any {
    return dataIn;
  }

  protected lookup(): void {
    if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
      this.decreeBasinsService
        .get(this.decreeBasinQueryParameters)
        .subscribe((data) => {
          this.decreeBasinQueryResult = this.postLookup(data);
          this.decreeBasinRows = data.results;
          this.decreeBasinRow = null;
          this.decreeBasinDataFound = data.totalElements > 0;
        });
    } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
      this.searchApplicationsService
        .get(this.applicationQueryParameters)
        .subscribe((data) => {
          this.applicationQueryResult = this.postLookup(data);
          this.applicationRows = data.results.map((app) => {
            if (
              app.applicationTypeCode !== undefined &&
              app.applicationTypeDescription !== undefined
            ) {
              app.applicationTypeDescription = `${app.applicationTypeCode} - ${app.applicationTypeDescription}`;
            }
            return app;
          });
          this.applicationRow = null;
          this.applicationDataFound = data.totalElements > 0;
        });
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

  public onSearch() {
    this.handleCurrentStep(2);
  }

  public stepping(step: StepperSelectionEvent): void {
    this.selectedStepIndex = step.selectedIndex;
    this.currentStep = step.selectedIndex;
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 2) {
        this.clearSelection();
      }
      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 2) {
        this.clearSelection();
        this.decreeBasinRows = null;
        this.applicationRows = null;
        if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
          this.decreeBasinQueryParameters.filters = {
            ...this.decreeBasinSearchForm.value,
          };
          this.decreeBasinQueryParameters.pageNumber = 1;
        } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
          this.applicationQueryParameters.filters = {
            ...this.applicationSearchForm.value,
          };
          this.applicationQueryParameters.pageNumber = 1;
        }

        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }

  public onRowClick(idx: number): void {
    this.selectedRow = idx;
    if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
      this.decreeBasinRow = this.decreeBasinRows[idx];
    } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
      this.applicationRow = this.applicationRows[idx];
    }
    this.formGroup.markAsDirty();
  }

  public onRowDoubleClick(idx: number): void {
    if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
      this.decreeBasinRow = this.decreeBasinRows[idx];
    } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
      this.applicationRow = this.applicationRows[idx];
    }
    this.save();
  }

  public onSortRequest(sort: Sort): void {
    if (sort) {
      if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
        this.decreeBasinQueryParameters.sortColumn = sort.active.toUpperCase();
        this.decreeBasinQueryParameters.sortDirection =
          sort.direction.toUpperCase();
      } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
        this.applicationQueryParameters.sortColumn = sort.active.toUpperCase();
        this.applicationQueryParameters.sortDirection =
          sort.direction.toUpperCase();
      }

      this.lookup();
    }
  }

  public onPaging(pagingOptions: PageEvent): void {
    if (pagingOptions) {
      if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
        this.decreeBasinQueryParameters.pageSize = pagingOptions.pageSize;
        this.decreeBasinQueryParameters.pageNumber =
          pagingOptions.pageIndex + 1;
      } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
        this.applicationQueryParameters.pageSize = pagingOptions.pageSize;
        this.applicationQueryParameters.pageNumber =
          pagingOptions.pageIndex + 1;
      }

      this.lookup();
    }
  }

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    if (!columns) return;
    const filterFunction =
      this.mode === DataManagementDialogModes.Update
        ? (field) => field?.displayInEdit ?? true
        : (field) => field?.displayInInsert ?? true;
    return columns.filter(filterFunction).map((item) => ({
      ...item,
    }));
  }

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

  public _onChange($event) {
    if ($event.fieldName === 'caseType' && !this.data.attachedApplicationId) {
      this.formWasInitialized = false;
      this.currentCaseType = $event.value;
      this.currentProgramType = this.data.programsDictionary[$event.value];

      if ($event.value === 'ARMR') {
        this.formGroup.removeControl('waterCourtCaseNumber');
        this.formGroup.removeControl('regionalOfficeId');
        this.formGroup.removeControl('caseStatus');
        return;
      }

      if (this.currentProgramType === this.programTypes.WC_PROGRAM) {
        this.applicationSearchForm = new FormGroup({});
      } else if (this.currentProgramType === this.programTypes.NA_PROGRAM) {
        this.decreeBasinSearchForm = new FormGroup({});
        this.formGroup.removeControl('waterCourtCaseNumber');
      }
      setTimeout(() => {
        this.formWasInitialized = true;
      });
    }
  }

  public save(): void {
    const dto = {
      ...this.formGroup.getRawValue(),
      programType: this.currentProgramType,
    };
    if (this.currentProgramType === CaseProgramTypes.WC_PROGRAM) {
      dto.decreeId = this.decreeBasinRow?.decreeId;
    }

    if (this.currentProgramType === CaseProgramTypes.NA_PROGRAM) {
      dto.applicationId =
        this.applicationRow?.applicationId || this.data.attachedApplicationId;
    }

    this.dialogRef.close(dto);
  }

  public clearSelection() {
    this.selectedRow = null;
    // Decrees
    this.decreeBasinRow = null;
    // Applications
    this.applicationRow = null;
  }
}
