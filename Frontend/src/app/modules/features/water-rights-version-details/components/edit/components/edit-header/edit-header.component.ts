import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, ReplaySubject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { WaterRightType } from 'src/app/modules/features/applications/components/edit/components/water-rights/components/water-rights-table/water-rights-table.component';
import { WaterRightStatusesService } from 'src/app/modules/shared/services/water-right-statuses.service';
import { VersionTypesService } from 'src/app/modules/features/water-rights/services/version-types.service';
import { VersionService } from 'src/app/modules/features/water-rights/services/version.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import {
  ReportDefinition,
  ReportTypes,
} from 'src/app/modules/shared/interfaces/report-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    './edit-header.component.scss',
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
  ],
  providers: [VersionService, WaterRightStatusesService, VersionTypesService],
})
export class EditVersionHeaderComponent
  extends DataRowComponent
  implements OnDestroy
{
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<void> = new EventEmitter<void>();
  @Output() scrollApplicationEvent: EventEmitter<void> =
    new EventEmitter<void>();
  @Input() reloadHeaderData: Observable<any> = null;
  private reloadHeaderData$: Subscription;
  public error;
  public data;
  public waterRightId;
  public versionId;
  public dialogWidth = '350px';
  public canEdit = true;

  constructor(
    public service: VersionService,
    private waterRightStatusesService: WaterRightStatusesService,
    public typeService: VersionTypesService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: Title,
    private sessionStorage: SessionStorageService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeWaterRightNumber',
      title: 'Water Right #',
      type: FormFieldTypeEnum.Input,
      editable: false,
      width: 160,
      dblClickable: true,
    },
    {
      columnId: 'waterRightTypeDescription',
      title: 'Water Right Type',
      type: FormFieldTypeEnum.Input,
      width: 320,
      displayInEdit: false,
    },
    {
      columnId: 'priorityDate',
      title: 'Priority Date and Time ',
      type: FormFieldTypeEnum.DateTime,
      displayInEdit: false,
      width: 190,
    },
    {
      columnId: 'completeVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      width: 350,
    },
    {
      columnId: 'versionTypeCode',
      title: 'Version Type',
      type: FormFieldTypeEnum.Select,
      width: 140,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'operatingAuthority',
      title: 'Operating Authority Date',
      type: FormFieldTypeEnum.Date,
      width: 180,
    },
    {
      columnId: 'versionStatusDescription',
      title: 'Version Status',
      type: FormFieldTypeEnum.Input,
      width: 140,
      displayInEdit: false,
    },
    {
      columnId: 'versionStatusCode',
      title: 'Version Status',
      type: FormFieldTypeEnum.Select,
      width: 140,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];

  private validWRTypes = ['HDRT', 'IRRD', 'ITSC', 'PRDL', 'RSCL', 'STOC'];
  private changeVersionTypes = ['CHAU', 'CHSP', 'REDU'];

  public reportTitle = 'Water Right Versions Reports';

  public reports: ReportDefinition[] = [
    {
      title: 'Change Authorization General Abstract',
      reportId: 'WRD2080AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.version;
      },
    },
    {
      title: 'Claims Examination Worksheet',
      reportId: 'WRD3010R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.version;
      },
      isAvailable: (data) =>
        this.validWRTypes.includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode),
    },
    {
      title: 'General Abstract',
      reportId: 'WRD2090AR',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.version;
      },
    },
    {
      title: 'Modified Abstract For Water Court',
      reportId: 'WRD2040R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.version;
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
        report.params.P_VERS_ID_SEQ = data.version;
        report.params.P_USERNAME = this.sessionStorage.username;
      },
      isAvailable: (data) =>
        this.validWRTypes.includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode),
    },
    {
      title: 'Scanned Documents',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.Basin = data.basin ?? '';
        report.params.WR_Number = data.waterRightNumber ?? '';
        report.params.Extension = data.ext ?? '';
        report.params.WR_Type = data.waterRightTypeDescription ?? '';
      },
      type: ReportTypes.SCANNED,
      isAvailable: (data) =>
        data.version === 1 ||
        (data.version > 1 &&
          !data.versionTypeCode.includes('CHAU') &&
          !data.versionTypeCode.includes('REDU')),
    },
    {
      title: 'Water Court Abstract',
      reportId: 'WRD2041R',
      setParams: (report: ReportDefinition, data: any): void => {
        report.params.P_WRGT_ID_SEQ = data.waterRightId;
        report.params.P_VERS_ID_SEQ = data.version;
      },
      isAvailable: (data) =>
        [...this.validWRTypes, 'CMPT'].includes(data.waterRightTypeCode) &&
        !this.changeVersionTypes.includes(data.versionTypeCode) &&
        data.canPrintDecreeReport,
    },
  ];

  public initFunction() {
    this.waterRightId = this.route.snapshot.params.waterRightId;
    this.versionId = this.route.snapshot.params.versionId;
    this.idArray = [this.waterRightId, this.versionId];
    this._get();
    if (this.reloadHeaderData) {
      this.reloadHeaderData$ = this.reloadHeaderData.subscribe(() => {
        this._get();
      });
    }
  }

  public ngOnDestroy() {
    super.ngOnDestroy();
    if (this.reloadHeaderData$) {
      this.reloadHeaderData$.unsubscribe();
    }
  }

  public _getHelperFunction(data: any) {
    this.populateWaterRightStatuses(data.get);
    if (data.get.operatingAuthority) {
      this._getColumn('operatingAuthority').validators = [Validators.required];
    }

    this.titleService.setTitle(
      `WRIS - Water Right: ${data.get.completeWaterRightNumber}, Version: ${data.get.version}`
    );

    // Decree and other permissions
    if (data.get.isVersionLocked) {
      this.canEdit = data.get.isEditableIfDecreed;
    } else {
      if (
        ['POST', 'REXM'].includes(data.get.versionTypeCode) &&
        !(data.get.canReexamineDecree || data.get.isEditableIfDecreed)
      ) {
        this.canEdit = false;
      } else if (
        ['SPPD'].includes(data.get.versionTypeCode) &&
        !(
          data.get.canReexamineDecree ||
          data.get.isEditableIfDecreed ||
          data.get.canModifySplitDecree
        )
      ) {
        this.canEdit = false;
      } else if (
        ['FINL'].includes(data.get.versionTypeCode) &&
        !data.get.isEditableIfDecreed
      ) {
        this.canEdit = false;
      }
    }

    // need to add canEdit to this for teh downstream children; otherwise they need to do the same math above
    this.dataEvent.emit({ ...data.get, canEdit: this.canEdit });
    return data.get;
  }

  public _onGetErrorHandler(error: HttpErrorResponse) {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Water Right Version not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Water Right Version',
        columns: this.columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        delete result.completeWaterRightNumber;
        this._update({
          ...result,
          standardsUpdated: this.data.standardsUpdated,
        });
      }
    });
  }

  public populateDropdowns(): void {
    this.observables.types = new ReplaySubject(1);
    this.typeService
      .get(this.queryParameters)
      .subscribe((types: { results: any[] }) => {
        this._getColumn('versionTypeCode').selectArr = types.results.map(
          (type) => ({
            value: type.value,
            name: type.description,
          })
        );
        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  private populateWaterRightStatuses(data) {
    const waterRightTypeCode = data.waterRightTypeCode;
    this.waterRightStatusesService
      .get(this.queryParameters, waterRightTypeCode)
      .subscribe((waterRightStatuses: { results: WaterRightType[] }) => {
        const statuses = waterRightStatuses.results
          .filter((type: WaterRightType) => type.value !== 'N/A')
          .map((type: WaterRightType) => ({
            name: type.description,
            value: type.value,
          }));
        this._getColumn('versionStatusCode').selectArr = statuses;
      });
  }

  public onFieldDblClick(field: ColumnDefinitionInterface) {
    if (field.columnId === 'completeWaterRightNumber') {
      this.router.navigate(['wris', 'water-rights', this.waterRightId]);
    }
  }

  public onApplicationLink(): void {
    if (this.data?.singleApplication) {
      void this.router.navigate([
        'wris',
        'applications',
        this.data.singleApplication,
      ]);
    } else {
      this.scrollApplicationEvent.next();
    }
  }
}
