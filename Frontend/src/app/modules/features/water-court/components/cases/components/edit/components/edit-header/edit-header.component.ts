import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { CaseStatusService } from 'src/app/modules/features/code-tables/components/case-status/services/case-status-types.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { Office } from 'src/app/modules/shared/components/templates/file-location-processor/file-location-processor.component';
import { OfficeService } from 'src/app/modules/shared/components/templates/file-location-processor/services/office.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { CaseTypeValuesService } from '../../../../services/case-type-values.service';
import { CasesAndHearingsService } from '../../../../services/cases-and-hearings.service';
import { CreateComponent } from '../../../create/create.component';
import { CaseProgramTypes } from '../../../create/enums/caseProgramTypes';
import { UpdateCasesDialogComponent } from './components/update-cases-dialog/update-cases-dialog.component';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [
    CasesAndHearingsService,
    CaseTypeValuesService,
    OfficeService,
    CaseStatusService,
    SessionStorageService,
  ],
})
export class EditHeaderComponent extends DataRowComponent {
  @ViewChild('firstInsert', { static: false }) firstInsert: MatButton;
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();

  public error;
  public dialogWidth = '400px';
  public data;

  constructor(
    public service: CasesAndHearingsService,
    public caseTypesService: CaseTypeValuesService,
    public caseStatusService: CaseStatusService,
    public officeService: OfficeService,
    public endpointService: EndpointsService,
    private sessionStorage: SessionStorageService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Cases/Hearings: ${this.route.snapshot.params.caseId}`
    );
  }

  private programsDictionary = {};
  // if issuedDateIsExpired === true the the system paints the field red
  public issuedDateIsExpired = false;
  public programTypes = CaseProgramTypes;
  public generalColumns: string[] = [
    'caseNumber',
    'caseTypeDescription',
    'caseType',
    'officeDescription',
    'officeId',
      'assignedTo',
    'caseStatusDescription',
    'caseStatus',
  ];
  public naAvailableColumns: string[] = [
    'applicationId',
    'basin',
    'completeApplicationType',
    'caseStatusDescription',
  ];
  public wcAvailableColumns: string[] = [
    'waterCourtCaseNumber',
    'decreeBasin',
    'decreeTypeDescription',
    'decreeIssueDate'
  ];
  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseNumber',
      title: 'Case #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      width: 100,
      displayInEdit: false,
    },
    {
      columnId: 'caseTypeDescription',
      title: 'Case Type',
      type: FormFieldTypeEnum.Input,
      width: 350,
      displayInEdit: false,
    },
    {
      columnId: 'caseType',
      title: 'Case Type',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'waterCourtCaseNumber',
      title: 'Water Court Case #',
      type: FormFieldTypeEnum.Input,
      width: 380,
      validators: [Validators.required, Validators.maxLength(20)],
    },
    {
      columnId: 'officeDescription',
      title: 'Regional Office',
      type: FormFieldTypeEnum.Input,
      width: 350,
      displayInEdit: false,
    },
    {
      columnId: 'officeId',
      title: 'Regional Office',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'decreeBasin',
      title: 'Decree Basin',
      type: FormFieldTypeEnum.Input,
      width: 350,
      displayInEdit: false,
    },
    {
      columnId: 'decreeTypeDescription',
      title: 'Decree Type',
      type: FormFieldTypeEnum.Input,
      width: 350,
      displayInEdit: false,
    },
    {
      columnId: 'applicationId',
      title: 'Application #',
      type: FormFieldTypeEnum.Input,
      width: 125,
      displayInEdit: false,
      dblClickable: true,
    },
    {
      columnId: 'basin',
      title: 'Basin',
      type: FormFieldTypeEnum.Input,
      width: 80,
      displayInEdit: false,
    },
    {
      columnId: 'completeApplicationType',
      title: 'Application Type Description',
      type: FormFieldTypeEnum.Input,
      width: 700,
      displayInEdit: false,
    },
    {
      columnId: 'decreeIssueDate',
      title: 'Decree Issued Date',
      type: FormFieldTypeEnum.Date,
      width: 150,
      displayInEdit: false,
    },
    {
      columnId: 'assignedTo',
      title: 'Currently Assigned To',
      type: FormFieldTypeEnum.Input,
      width: 450,
      displayInEdit: false,
    },
    {
      columnId: 'caseStatusDescription',
      title: 'Case Status',
      type: FormFieldTypeEnum.Input,
      width: 450,
      displayInEdit: false,
    },
    {
      columnId: 'caseStatus',
      title: 'Case Status',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
  ];

  private createColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'caseType',
      title: 'Case Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInSearch: false,
    },
    {
      columnId: 'waterCourtCaseNumber',
      title: 'Water Court Case Number',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required, Validators.maxLength(20)],
      displayInSearch: false,
    },
    {
      columnId: 'regionalOfficeId',
      title: 'Regional Office',
      type: FormFieldTypeEnum.Select,
      displayInSearch: false,
    },
    {
      columnId: 'caseStatus',
      title: 'Case Status',
      type: FormFieldTypeEnum.Select,
      displayInSearch: false,
    },
  ];

  public reportTitle = 'Case Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
    },
    {
      title: 'Water Court Abstract',
      reportId: 'WRD2041R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
    },
    {
      title: 'Water Court Data Sheet',
      reportId: 'WRD4005R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
    },
    {
      title: 'Water Court Registry List',
      reportId: 'WRD4007R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
    },
    {
      title: 'Modified Abstract for Water Court',
      reportId: 'WRD2040R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
      isAvailable: (data) =>
        (this.sessionStorage.officeId === 11 || data.canPrintAllWcReports) &&
        data.canPrintDecreeReport,
    },
    {
      title: 'Application Party List',
      reportId: 'WRD3020R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
      isAvailable: (data) => ['ADM', 'WCRT'].includes(data.caseType),
    },
    {
      title: 'Preliminary Application Party List',
      reportId: 'WRD3021R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
      },
      isAvailable: (data) => ['ADM', 'WCRT'].includes(data.caseType),
    },
    {
      title: 'Hearings Case List',
      reportId: 'WRD4015R',
      setParams: (report: ReportDefinition, data: any): void => {},
    },
    {
      title: 'Decree Cases',
      reportId: 'WRD2021R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_CASE_ID_SEQ = data.caseNumber;
        report.params.P_DECR_ID_SEQ = data.decreeId;
      },
      isAvailable: (data) =>
        (this.sessionStorage.officeId === 11 || data.canPrintAllWcReports) &&
        data.canPrintDecreeReport &&
        !!data.decreeId,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.caseId];
    this._get();
  }

  protected _getHelperFunction(data: any) {
    this.dataEvent.emit(data);

    this.issuedDateIsExpired = data.get?.decreeIssueDate
      ? moment(data.get.decreeIssueDate).isBefore(moment('2003-01-22'))
      : false;

    if (data.get.applications?.length) {
      return {
        ...data.get,
        ...data.get.applications[0],
      };
    } else {
      this.issuedDateIsExpired = false;
    }

    return {
      ...data.get,
    };
  }

  private _getCreateColumn(columnId: string): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = this.createColumns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  protected populateDropdowns(): void {
    // Case Types
    this.observables.caseTypes = new ReplaySubject(1);

    this.caseTypesService
      .get({
        ...this.queryParameters,
        filters: {
          supported: '1',
        },
      })
      .subscribe((types) => {
        const typesArr = [];
        for (let i = 0; i < types.results.length; i++) {
          // Push value to selectArr
          typesArr.push({
            name: types.results[i].description,
            value: types.results[i].value,
          });
          // Add value to programs dictionary
          this.programsDictionary[types.results[i].value] =
            types.results[i].program;
        }

        this._getColumn('caseType').selectArr = typesArr;
        this._getCreateColumn('caseType').selectArr = typesArr;
        this.observables.caseTypes.next(typesArr);
        this.observables.caseTypes.complete();
      });
    // Case Statuses
    this.observables.caseStatuses = new ReplaySubject(1);

    this.caseStatusService
      .get({ sortColumn: 'description', sortDirection: 'asc' })
      .subscribe((statuses) => {
        statuses.results.unshift({
          value: null,
          description: null,
        });
        const statusesArr = statuses.results.map(
          (status: { code: string; description: string }) => ({
            name: status.description,
            value: status.code,
          })
        );

        this._getColumn('caseStatus').selectArr = statusesArr;
        this._getCreateColumn('caseStatus').selectArr = statusesArr;
        this.observables.caseStatuses.next(statuses);
        this.observables.caseStatuses.complete();
      });
    // Regional Offcies
    this.observables.offices = new ReplaySubject(1);

    this.officeService
      .get(this.queryParameters)
      .subscribe((offices: { results: Office[] }) => {
        offices.results.unshift({
          officeId: null,
          description: null,
        });
        const officesArr = offices.results.map((office: Office) => ({
          name: office.description,
          value: office.officeId,
        }));
        this._getCreateColumn('regionalOfficeId').selectArr = officesArr;
        this._getColumn('officeId').selectArr = officesArr;

        this.observables.offices.next(offices);
        this.observables.offices.complete();
      });
  }

  protected _update(updatedRow: any, extraData = {}): void {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      () => {
        this._get();
        this.snackBar.open('Record successfully updated.', null);
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayEditDialog({
          ...updatedRow,
          ...extraData,
        });
      }
    );
  }

  protected _displayEditDialog(data?: any): void {
    let currentAvailableTypes = this._getColumn('caseType').selectArr.map(
      (type) => {
        return { ...type };
      }
    );
    const currentTypeIndex = (currentAvailableTypes as any[]).findIndex(
      (evt) => {
        return evt.value === data.caseType;
      }
    );
    if (currentTypeIndex === -1) {
      currentAvailableTypes.unshift({
        name: data.caseTypeDescription,
        value: data.caseType,
      });
      this.programsDictionary[data.caseType] = data.programType;
      currentAvailableTypes = currentAvailableTypes.sort((a, b) => {
        if (a.name > b.name) {
          return 1;
        }
        if (a.name < b.name) {
          return -1;
        }
        return 0;
      });
    }
    // Create a copy of the columns and attach the new selectArr
    const columns = this.columns.map((col) => {
      return col.columnId === 'caseType'
        ? { ...col, selectArr: currentAvailableTypes }
        : { ...col };
    });
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateCasesDialogComponent, {
      width: 'auto',
      data: {
        title: `Update Case/Hearing`,
        columns: columns,
        values: data,
        programsDictionary: this.programsDictionary,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(
          this._buildEditDto(data, result?.requestData),
          result.extraData
        );
      }
      this.editButton.focus();
    });
  }

  private _insert(newRow: any): void {
    this.service.insert(newRow).subscribe(
      (dto) => {
        let messages = ['Record successfully added.'];
        if (!!dto?.messages) {
          messages = [...dto.messages, ...messages];
        }
        this.snackBar.open(messages.join('\n'));
        void this.router.navigate(['..', dto?.caseNumber], {
          relativeTo: this.route,
        });
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error as ErrorBodyInterface;
        let message = 'Cannot insert new record. ';
        message += errorBody.userMessage || ErrorMessageEnum.POST;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayInsertDialog(newRow);
      }
    );
  }

  private _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(CreateComponent, {
      width: 'auto',
      data: {
        title: 'Create New Case or Hearing Record',
        mode: DataManagementDialogModes.Insert,
        createColumns: this.createColumns,
        programsDictionary: this.programsDictionary,
        values: {
          ...data,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  // Handle the onInsert event
  public onInsert(): void {
    this._displayInsertDialog(null);
  }

  private deleteCase() {
    this.service.delete(this.data.caseNumber).subscribe(() => {
      this.snackBar.open('Record successfully deleted.');
      void this.router.navigate(['..'], {
        relativeTo: this.route,
      });
    });
  }

  public onDelete(): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: {
        title: 'Delete Case/Hearing',
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this.deleteCase();
      }
    });
  }

  protected onFieldDblClickHandler(column: ColumnDefinitionInterface): void {
    if (column.columnId === 'applicationId' && this.data.applicationId) {
      void this.router.navigate([
        'wris',
        'applications',
        this.data.applicationId,
      ]);
    }
  }

  protected _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Case/Hearing not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }
}
