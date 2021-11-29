import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ExaminationsService } from './services/examinations.service';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    './edit-header.component.scss',
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [ExaminationsService, StaffService, SessionStorageService],
})
export class EditHeaderComponent extends DataRowComponent {
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();
  public error;
  public dialogWidth = '400px';
  public data;

  constructor(
    public service: ExaminationsService,
    public staffsService: StaffService,
    private sessionStorage: SessionStorageService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  private validWRTypes = ['HDRT', 'IRRD', 'ITSC', 'PRDL', 'RSCL', 'STOC'];
  private changeVersionTypes = ['CHAU', 'CHSP', 'REDU'];

  public reportTitle = 'Examination Reports';
  public reports: ReportDefinition[] = [
    {
      title: 'Claims Examination Worksheet',
      reportId: 'WRD3010R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.versionNumber;
      },
      isAvailable: (data) =>
        this.validWRTypes.includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode),
    },
    {
      title: 'Modified Abstract for Water Court',
      reportId: 'WRD2040R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.versionNumber;
      },
      isAvailable: (data) =>
        [...this.validWRTypes, 'CMPT'].includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode) &&
        data.canPrintDecreeReport,
    },
    {
      title: 'Review Abstract',
      reportId: 'WRD2030R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.versionNumber;
        report.params.P_USERNAME = this.sessionStorage.username;
      },
      isAvailable: (data) =>
        this.validWRTypes.includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode),
    },
    {
      title: 'Water Court Abstract',
      reportId: 'WRD2041R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.versionNumber;
      },
      isAvailable: (data) =>
        [...this.validWRTypes, 'CMPT'].includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode) &&
        data.canPrintDecreeReport,
    },
  ];

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      fontWeight: 700,
      width: 190,
      dblClickable: true,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      width: 320,
    },
    {
      columnId: 'waterRightStatusDescription',
      title: 'Water Right Status',
      type: FormFieldTypeEnum.Input,
      width: 160,
    },
    {
      columnId: 'versionNumber',
      title: 'Version #',
      width: 90,
      type: FormFieldTypeEnum.Input,
      dblClickable: true,
    },
  ];

  public contactPointColumns = [
    {
      columnId: 'cntPos',
      title: 'Plus (+)',
      type: FormFieldTypeEnum.Input,
      width: 180,
    },
    {
      columnId: 'cntNeg',
      title: 'Minus (-)',
      type: FormFieldTypeEnum.Input,
      width: 180,
    },
    {
      columnId: 'totalClaimedAcres',
      title: 'Total Claimed Irrigation Acres',
      type: FormFieldTypeEnum.Input,
    },
  ];

  public examinerPeriodColumns = [
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      width: 140,
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.beforeOtherField('endDate', 'End Date'),
        WRISValidators.dateBeforeToday,
      ],
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      width: 140,
      validators: [
        WRISValidators.afterOtherField('beginDate', 'Begin Date'),
        WRISValidators.dateBeforeToday,
      ],
      type: FormFieldTypeEnum.Date,
    },
  ];

  public examinerColumns = [
    {
      columnId: 'name',
      title: 'Examiner',
      type: FormFieldTypeEnum.Input,
      width: 320,
      displayInEdit: false,
    },
    {
      columnId: 'dnrcId',
      title: 'Examiner',
      type: FormFieldTypeEnum.Autocomplete,
      width: 320,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];

  public examinerFormColumns = [
    ...this.examinerColumns,
    ...this.examinerPeriodColumns,
  ];

  protected initFunction() {
    this.idArray = [this.route.snapshot.params.examinationId];
    this._get();
  }

  protected _getHelperFunction(data: any) {
    this.dataEvent.emit(data);
    this.titleService.setTitle(
      `WRIS - Examination: ${data.get.completeWaterRightNumber ?? ''}`
    );

    return {
      ...data.get,
    };
  }

  protected populateDropdowns(): void {
    this.observables.staffs = new ReplaySubject(1);

    this.staffsService.get(this.queryParameters).subscribe((staffs) => {
      this._getColumn('dnrcId').selectArr = staffs.results.map(
        (staff: { staffId: number; name: string }) => ({
          name: staff.name,
          value: staff.staffId,
        })
      );
      this.observables.staffs.next(staffs);
      this.observables.staffs.complete();
    });
  }

  protected _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Examination not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data?: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Examiner',
        columns: this.examinerFormColumns,
        values: this.data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._update(result);
      }
    });
  }

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    return [
      ...this.columns,
      ...this.contactPointColumns,
      ...this.examinerFormColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }

  private redirectToWaterRight(waterRightId) {
    void this.router.navigate(['wris', 'water-rights', waterRightId]);
  }

  private redirectToWaterRightVersion(waterRightId, version): void {
    void this.router.navigate([
      'wris',
      'water-rights',
      waterRightId,
      'versions',
      version,
    ]);
  }

  protected onFieldDblClickHandler(column: ColumnDefinitionInterface): void {
    if (column.columnId === 'completeWaterRightNumber') {
      this.redirectToWaterRight(this.data.waterRightId);
    }
    if (column.columnId === 'versionNumber') {
      this.redirectToWaterRightVersion(
        this.data.waterRightId,
        this.data.versionNumber
      );
    }
  }
}
