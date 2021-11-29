/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, ReplaySubject, Subject, Subscription } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SessionStorageService } from 'src/app/modules/core/services/session-storage/session-storage.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { ReportDefinition } from 'src/app/modules/shared/interfaces/report-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { Purpose } from '../../../../interfaces/purpose.interface';
import { PurposeTypesEnum } from '../../../../shared/purpose-types.enum';
import { PurposesService } from '../../../search/services/purposes.service';
import { PurposeInsertOrEditDialogComponent } from './components/purpose-insert-dialog/purpose-insert-or-edit-dialog.component';
import { WaterRightVersionPurposesService } from './services/water-right-version-purposes.service';
import { PurposeDropdownsService } from './services/purpose-dropdowns.service';
import { PurposeExaminationService } from './services/purpose-examination.service';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { StaffService } from 'src/app/modules/shared/components/templates/file-location-processor/services/staff.service';
import { PodDropdownService } from 'src/app/modules/features/water-rights-version-details/components/edit/components/point-of-diversion/services/pod-dropdown.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-edit-header',
  templateUrl: './edit-header.component.html',
  styleUrls: [
    '../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './edit-header.component.scss',
  ],
  providers: [
    WaterRightVersionPurposesService,
    PurposesService,
    SessionStorageService,
    PurposeExaminationService,
    StaffService,
  ],
})
export class EditPurposeHeaderComponent
  extends DataRowComponent
  implements OnDestroy
{
  // Variables the fixed header needs in order to work properly
  @ViewChild('firstInsert', { static: false }) firstInsert: MatButton;
  @Output()
  errorEvent: EventEmitter<HttpErrorResponse> = new EventEmitter<HttpErrorResponse>();
  @Output() dataEvent: EventEmitter<Purpose> = new EventEmitter<Purpose>();
  @Input() reloadHeaderData: Observable<any> = null;
  private reloadHeaderData$: Subscription;
  public error;
  public data;
  public showAdditionalPurposeInformation = false;
  private unsubscribe = new Subject();

  constructor(
    public service: PurposesService,
    public staffsService: StaffService,
    public purposeExaminationService: PurposeExaminationService,
    public waterRightVersionPurposesService: WaterRightVersionPurposesService,
    private sessionStorage: SessionStorageService,
    public dropdownService: PurposeDropdownsService,
    public podDropdownService: PodDropdownService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private route: ActivatedRoute,
    private router: Router,
    private titleService: Title
  ) {
    super(service, endpointService, dialog, snackBar);
    this.titleService.setTitle(
      `WRIS - Purposes: ${this.route.snapshot.params.purposeId}`
    );
  }

  private validWRTypes = ['HDRT', 'IRRD', 'ITSC', 'PRDL', 'RSCL', 'STOC'];
  private changeVersionTypes = ['CHAU', 'CHSP', 'REDU'];
  private invalidWRTypesForExaminations = [
    '62GW',
    'EXEX',
    'GWCT',
    'PRPM',
    'TPRP',
    'STWP',
    'WRWR',
    'CDWR',
    'NAPP',
    'NFWP',
  ];
  private invalidVersionTypesForExaminations = ['CHAU', 'REDU', 'REDX', 'CHSP'];
  public disableExaminations = false;
  public canEdit = true;

  public reportTitle = 'Purpose Reports';

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
      title: 'Modified Abstract For Water Court',
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

  // Column fields
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
      columnId: 'completeWaterRightVersion',
      title: 'Version',
      type: FormFieldTypeEnum.Input,
      width: 440,
      dblClickable: true,
    },
    {
      columnId: 'completePurposeCode',
      title: 'Purpose',
      type: FormFieldTypeEnum.Input,
      width: 490,
      dblClickable: true,
    },
    {
      columnId: 'clarification',
      title: 'Purpose Clarification',
      type: FormFieldTypeEnum.Input,
      width: 490,
    },
    {
      columnId: 'purposeOriginDescription',
      title: 'Purpose Origin',
      type: FormFieldTypeEnum.Input,
      width: 270,
    },
    {
      columnId: 'purposeVolume',
      title: 'Purpose Volume',
      type: FormFieldTypeEnum.Input,
      width: 140,
    },
    {
      columnId: 'modifiedByThisChangeDescription',
      title: 'Modified By This Change',
      type: FormFieldTypeEnum.Input,
      width: 180,
    },
  ];

  public examinerColumns = [
    {
      columnId: 'dnrcId',
      title: 'Examiner',
      type: FormFieldTypeEnum.Autocomplete,
      width: 320,
      displayInTable: false,
      validators: [Validators.required],
    },
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

  // These columns are dynamically added in the _getHelperFunction
  public purposeAdditionalColumns: ColumnDefinitionInterface[] = [];

  private dialogFormColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'purposeCode',
      title: 'Purpose',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'irrigationCode',
      title: 'Irrigation Type',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
    },
    {
      columnId: 'purposeOrigin',
      title: 'Purpose Origin',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'modifiedByThisChange',
      title: 'Modified By This Change',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'climaticCode',
      title: 'Climatic Area',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'rotation',
      title: 'Crop Rotation',
      type: FormFieldTypeEnum.Select,
    },
    {
      columnId: 'clarification',
      title: 'Purpose Clarification',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.maxLength(100)],
      displayInInsert: false,
    },
    {
      columnId: 'purposeVolume',
      title: 'Purpose Volume',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      displayInInsert: false,
    },
    {
      columnId: 'animalUnits',
      title: 'Animal Units',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(6, 1)],
    },
    {
      columnId: 'household',
      title: 'Household Units',
      type: FormFieldTypeEnum.Input,
    },
  ];
  // Acreage columns
  private placeOfUseColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'acreage',
      title: 'Acreage',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      formWidth: 160,
    },
    {
      columnId: 'elementOrigin',
      title: 'Place Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      formWidth: 260,
    },
    {
      columnId: 'elementOriginDescription',
      title: 'Place Origin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
    {
      columnId: 'completeLegalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
      width: 300,
      displayInEdit: false,
      displayInInsert: false,
    },
  ];

  private firstLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'governmentLot',
      title: 'Govt Lot',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 160,
      validators: [WRISValidators.isNumber(3, 0)],
    },
    {
      columnId: 'description40',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
    },
    {
      columnId: 'description80',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
    },
    {
      columnId: 'description160',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
    },
    {
      columnId: 'description320',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
    },
  ];

  private secondLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'section',
      title: 'Sec',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 120,
      validators: [WRISValidators.isNumber(2, 0), Validators.required],
    },
    {
      columnId: 'township',
      title: 'Twp',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 130,
      validators: [WRISValidators.isNumber(3, 1), Validators.required],
    },
    {
      columnId: 'townshipDirection',
      title: 'N/S',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      formWidth: 100,
      validators: [Validators.required],
    },
    {
      columnId: 'range',
      title: 'Rge',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 110,
      validators: [WRISValidators.isNumber(3, 1), Validators.required],
    },
    {
      columnId: 'rangeDirection',
      title: 'E/W',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      formWidth: 80,
      validators: [Validators.required],
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 230,
      validators: [Validators.required, WRISValidators.isNumber(10, 0)],
    },
  ];
  // Period data
  private periodColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'beginDate',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.beforeOtherField('endDate', 'End Date'),
      ],
      displayInTable: false,
    },
    {
      columnId: 'periodBegin',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'endDate',
      title: 'End Date',
      type: FormFieldTypeEnum.Date,
      validators: [
        Validators.required,
        WRISValidators.afterOtherField('beginDate', 'Begin Date'),
      ],
      displayInTable: false,
    },
    {
      columnId: 'periodEnd',
      title: 'End Date',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'elementOrigin',
      title: 'Period Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'elementOriginDescription',
      title: 'Period Origin',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
  ];

  protected initFunction(): void {
    this.idArray = [this.route.snapshot.params.purposeId];

    if (this.reloadHeaderData) {
      this.reloadHeaderData$ = this.reloadHeaderData.subscribe(() => {
        this._get();
      });
    }

    this._get();
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.reloadHeaderData$) {
      this.reloadHeaderData$.unsubscribe();
    }
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPOST value
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(
        this.waterRightVersionPurposesService.url
      ),
    };
  }

  protected _getHelperFunction(data: { get: Purpose }): { [key: string]: any } {
    /*
      This query to retrieve a specific purpose only uses the purpose ID from the URL. Thus, the user could manually
      change the water right id and/or the version id in the URL and get the same result. However, the URL would
      be invalid. This guard prevents that.
    */
    if (
      +this.route.snapshot.params.versionId !== +data.get.versionNumber ||
      +this.route.snapshot.params.waterRightId !== +data.get.waterRightId
    ) {
      this.error = true;
      this.errorEvent.emit(null);
      const message =
        'Purpose not found.\nThe Water Right Id or Version Id in the browser URL do not match the data in the database.\n' +
        'If either of these values were manaully changed, they are incorrect.';
      this.snackBar.open(message, 'Dismiss', 0);
      // Return an empty dataset
      return [];
    }

    this.purposeAdditionalColumns = [];
    // Show columns only if they have data
    if (data.get?.household) {
      this.purposeAdditionalColumns.push({
        columnId: 'household',
        title: 'Household',
        type: FormFieldTypeEnum.Input,
        width: 110,
      });
    }
    if (data.get?.animalUnits) {
      this.purposeAdditionalColumns.push({
        columnId: 'animalUnits',
        title: 'Animal Units',
        type: FormFieldTypeEnum.Input,
        width: 140,
      });
    }
    if (data.get?.climaticCode) {
      this.purposeAdditionalColumns.push({
        columnId: 'climaticCodeDescription',
        title: 'Climatic Area',
        type: FormFieldTypeEnum.Input,
        width: 230,
      });
    }
    if (data.get?.rotation) {
      this.purposeAdditionalColumns.push({
        columnId: 'rotationDescription',
        title: 'Crop Rotation',
        type: FormFieldTypeEnum.Input,
        width: 110,
      });
    }

    if (
      data.get?.applicationTypeCodes?.includes('650') &&
      !this._getDialogColumnsFromSpecificSource('leaseYear', this.periodColumns)
    ) {
      this.periodColumns = [
        ...this.periodColumns,
        {
          columnId: 'leaseYear',
          title: 'Lease Year',
          type: FormFieldTypeEnum.Select,
        },
      ];
      this._getColumn('leaseYear').selectArr =
        this.dropdownService.leaseYearValues;
    }

    if (
      data.get?.household ||
      data.get?.animalUnits ||
      data.get?.climaticCode ||
      data.get?.rotation
    ) {
      this.showAdditionalPurposeInformation = true;
    } else {
      this.showAdditionalPurposeInformation = false;
    }

    if (
      this.invalidWRTypesForExaminations.includes(
        data.get.waterRightTypeCode
      ) ||
      this.invalidVersionTypesForExaminations.includes(data.get.versionTypeCode)
    ) {
      this.disableExaminations = true;
    }

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

    this.dataEvent.emit({ ...data.get, canEdit: this.canEdit });
    return { ...data.get };
  }

  protected _onGetErrorHandler(error: HttpErrorResponse): void {
    this.error = true;
    this.errorEvent.emit(error);
    const errorBody = error.error as ErrorBodyInterface;
    let message = 'Purpose not found.\n';
    message += errorBody.userMessage || '';
    this.snackBar.open(message);
  }

  private _getDialogColumnsFromSpecificSource(
    columnId: string,
    columns
  ): ColumnDefinitionInterface {
    return columns.find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }

  protected _getColumn(columnId: string): ColumnDefinitionInterface {
    return [
      ...this.dialogFormColumns,
      ...this.periodColumns,
      ...this.placeOfUseColumns,
      ...this.firstLegalLandDescriptionColumns,
      ...this.secondLegalLandDescriptionColumns,
      ...this.examinerColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }

  protected populateDropdowns(): void {
    this._getDialogColumnsFromSpecificSource(
      'purposeOrigin',
      this.dialogFormColumns
    ).selectArr = this.dropdownService.ownerOrigins;

    this._getDialogColumnsFromSpecificSource(
      'elementOrigin',
      this.placeOfUseColumns
    ).selectArr = this.dropdownService.ownerOrigins;

    this._getDialogColumnsFromSpecificSource(
      'elementOrigin',
      this.periodColumns
    ).selectArr = this.dropdownService.ownerOrigins;

    this._getDialogColumnsFromSpecificSource(
      'modifiedByThisChange',
      this.dialogFormColumns
    ).selectArr = this.dropdownService.yesNoValues;

    if (+this.route.snapshot.params.versionId !== 1) {
      this.placeOfUseColumns = [
        ...this.placeOfUseColumns,
        {
          columnId: 'modifiedByThisChange',
          title: 'Modified By This Change',
          type: FormFieldTypeEnum.Select,
          displayInTable: false,
          selectArr: this.dropdownService.yesNoValues,
          formWidth: 200,
        },
        {
          columnId: 'modifiedByThisChangeDescription',
          title: 'Modified By This Change',
          type: FormFieldTypeEnum.Input,
          displayInEdit: false,
          displayInInsert: false,
        },
      ];
    }

    this._getDialogColumnsFromSpecificSource(
      'rotation',
      this.dialogFormColumns
    ).selectArr = this.dropdownService.yesNoValues;

    this._getColumn('purposeCode').selectArr =
      this.dropdownService.purposeCodes;
    this._getColumn('irrigationCode').selectArr =
      this.dropdownService.irrigationTypes;

    this._getColumn('climaticCode').selectArr =
      this.dropdownService.climaticAreas;

    this._getColumn('description40').selectArr =
      this.podDropdownService.aliquots;

    this._getColumn('description80').selectArr =
      this.podDropdownService.aliquots;

    this._getColumn('description160').selectArr =
      this.podDropdownService.aliquots;

    this._getColumn('description320').selectArr =
      this.podDropdownService.aliquots;

    this._getColumn('townshipDirection').selectArr =
      this.podDropdownService.townshipDirections;

    this._getColumn('rangeDirection').selectArr =
      this.podDropdownService.rangeDirections;

    this._getColumn('countyId').selectArr = this.podDropdownService.counties;

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

  private redirectToExaminationsPage(examinationId) {
    void this.router.navigate([
      'wris',
      'water-court',
      'examinations',
      examinationId,
    ]);
  }

  private openExaminationsCreate(): void {
    // Open the create dialog
    let currentStaff;
    this._getColumn('dnrcId').selectArr.forEach((staff) => {
      if (staff.name === this.sessionStorage.userFullName) {
        currentStaff = staff;
      }
    });
    const dialogRef = this.dialog.open(InsertDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Create Examination',
        columns: this.examinerColumns,
        values: {
          dnrcId: currentStaff?.value,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createExamination(result);
      }
      this.editButton.focus();
    });
  }

  public onClickExaminationHandler(): void {
    if (this.data.examinationId) {
      this.redirectToExaminationsPage(this.data.examinationId);
    } else {
      WaterRightsPrivileges.checkVersionDecree(
        Number(this.route.snapshot.params.versionId),
        this.data.isDecreed,
        this.data.isEditableIfDecreed,
        this.dialog,
        this.openExaminationsCreate.bind(this)
      );
    }
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

  private redirectToWaterRightVersionAndOpenPurposeAccordion(
    waterRightId,
    version
  ): void {
    void this.router.navigate(
      ['wris', 'water-rights', waterRightId, 'versions', version],
      {
        queryParams: { true: 'Y' },
      }
    );
  }

  private reditectToNewPurpose(purposeId): void {
    void this.router.navigate([
      'wris',
      'water-rights',
      this.route.snapshot.params.waterRightId,
      'versions',
      this.route.snapshot.params.versionId,
      'purposes',
      purposeId,
    ]);
  }

  protected onFieldDblClickHandler(column: ColumnDefinitionInterface): void {
    if (column.columnId === 'completeWaterRightNumber') {
      this.redirectToWaterRight(this.data.waterRightId);
    }
    if (column.columnId === 'completeWaterRightVersion') {
      this.redirectToWaterRightVersion(
        this.data.waterRightId,
        this.data.versionNumber
      );
    }
    if (column.columnId === 'completePurposeCode') {
      this.redirectToWaterRightVersionAndOpenPurposeAccordion(
        this.data.waterRightId,
        this.data.versionNumber
      );
    }
  }

  static householdValidator(waterRightTypeCode): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.parent) {
        if (waterRightTypeCode === 'GWCT') {
          if (
            control.parent.get('purposeCode').value ===
              PurposeTypesEnum.DOMESTIC &&
            control.value > 1
          ) {
            return {
              errorMessage:
                'Households cannot exceed 1 for a DOMESTIC purpose.',
            };
          }
          if (
            control.parent.get('purposeCode').value ===
              PurposeTypesEnum.MULTIPLE_DOMESTIC &&
            control.value < 2
          ) {
            return {
              errorMessage:
                'If households are less than 2 - purpose must be DOMESTIC.',
            };
          }
        }
      }

      return null;
    };
  }

  static purposeTypeValidator(
    waterRightTypeCode,
    versionNumber,
    applicationTypeCodes
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // GWCT === GROUND WATER CERTIFICATE // PRPM === PROVISIONAL PERMIT
      if (
        ['GWCT', 'PRPM'].includes(waterRightTypeCode) &&
        versionNumber === 1
      ) {
        // If the WR Version has one 647 application the Purpose Type must be Fire Protection
        if (
          applicationTypeCodes?.includes('647') &&
          control.value !== PurposeTypesEnum.FIRE_PROTECTION
        ) {
          return {
            errorMessage:
              'Only purpose type of Fire Protection is valid when attached to a 647 application.',
          };
        }
        // If the WR Version has one 646 application the Purpose Type must be Geothermal or Geothermal Heating
        if (
          applicationTypeCodes?.includes('646') &&
          ![
            PurposeTypesEnum.GEOTHERMAL,
            PurposeTypesEnum.GEOTHERMAL_HEATING,
          ].includes(control.value as PurposeTypesEnum)
        ) {
          return {
            errorMessage:
              'Only purpose type of Geothermal or Geothermal Heating is valid when attached to a 646 application.',
          };
        }
      }

      return null;
    };
  }

  static originValidator(
    waterRightTypeCode,
    waterRightStatusCode
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (
        waterRightTypeCode === 'CMPT' &&
        waterRightStatusCode !== 'N/A' &&
        control.value !== 'CMPT'
      ) {
        return {
          errorMessage:
            'The element of origin must be Compacted for a Compacted Water Right.',
        };
      }

      if (waterRightTypeCode !== 'CMPT' && control.value === 'CMPT') {
        return {
          errorMessage:
            'Compacted may not be used as an element of origin for a Water Right that is not Compacted.',
        };
      }

      return null;
    };
  }

  // Edit methods

  /*
   * Update the data using the data service
   */
  protected _update(updatedRow: any): void {
    this.service.update(updatedRow, ...this.idArray).subscribe(
      (dto) => {
        if (dto.calcVolWarnings) {
          const warnings = dto.calcVolWarnings
            .map((w) => w.warning)
            .join(' \n - ');
          const confirmationDialog = this.dialog.open(
            ConfirmationDialogComponent,
            {
              data: {
                title: 'Warning',
                message: `- ${warnings}`,
                confirmButtonName: 'Continue',
                hideCancelButton: true,
              },
            }
          );
          confirmationDialog.afterClosed().subscribe(() => {
            this._get();
            this.snackBar.open('Record successfully updated.', null);
          });
        } else {
          this._get();
          this.snackBar.open('Record successfully updated.', null);
        }
      },
      (err: HttpErrorResponse) => {
        const errorBody = err.error;
        let message = 'Cannot update record. ';
        message += errorBody.userMessage || ErrorMessageEnum.PUT;
        this.snackBar.open(message);

        // Redisplay the dialog with the input data
        this._displayEditDialog(updatedRow);
      }
    );
  }

  private setValidators(): void {
    this._getDialogColumnsFromSpecificSource(
      'purposeOrigin',
      this.dialogFormColumns
    ).validators = [
      Validators.required,
      EditPurposeHeaderComponent.originValidator(
        this.data?.waterRightTypeCode,
        this.data?.waterRightStatusCode
      ),
    ];
    this._getDialogColumnsFromSpecificSource(
      'purposeCode',
      this.dialogFormColumns
    ).validators = [
      Validators.required,
      EditPurposeHeaderComponent.purposeTypeValidator(
        this.data?.waterRightTypeCode,
        +this.data?.versionNumber,
        this.data?.applicationTypeCodes
      ),
    ];
    this._getDialogColumnsFromSpecificSource(
      'household',
      this.dialogFormColumns
    ).validators = [
      WRISValidators.isNumber(4, 0),
      EditPurposeHeaderComponent.householdValidator(
        this.data?.waterRightTypeCode
      ),
    ];
    this._getDialogColumnsFromSpecificSource(
      'elementOrigin',
      this.placeOfUseColumns
    ).validators = [
      Validators.required,
      EditPurposeHeaderComponent.originValidator(
        this.data?.waterRightTypeCode,
        this.data?.waterRightStatusCode
      ),
    ];
    this._getDialogColumnsFromSpecificSource(
      'elementOrigin',
      this.periodColumns
    ).validators = [
      Validators.required,
      EditPurposeHeaderComponent.originValidator(
        this.data?.waterRightTypeCode,
        this.data?.waterRightStatusCode
      ),
    ];
  }

  /*
   * Display the Update dialog and, if data is returned, call the update function
   */
  protected _displayEditDialog(data: any): void {
    this.setValidators();
    const dialogRef = this.dialog.open(PurposeInsertOrEditDialogComponent, {
      width: 'auto',
      data: {
        title: 'Update Purpose',
        mode: DataManagementDialogModes.Update,
        columns: this.dialogFormColumns,
        values: {
          ...data,
          versionNumber: this.data.versionNumber,
        },
      },
    });

    dialogRef.afterClosed().subscribe((result: Purpose) => {
      if (result !== null && result !== undefined) {
        this._update(result);
      } else {
        this.editButton.focus();
      }
    });
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.route.snapshot.params.versionId),
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  // Insert methods
  protected _buildInsertIdArray(dto: any): string[] {
    return [
      this.route.snapshot.params.waterRightId,
      this.route.snapshot.params.versionId,
    ];
  }

  protected _buildInsertDto(dto: any): any {
    return dto;
  }

  private createExamination(newRow: any): void {
    this.purposeExaminationService
      .insert(this._buildInsertDto(newRow), this.data.purposeId)
      .subscribe(
        (dto) => {
          let messages = ['Examination successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.redirectToExaminationsPage(dto.examinationId);
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot create Examination. ';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);
          this.onClickExaminationHandler();
        }
      );
  }

  /*
   * Insert the data using the data service
   */
  protected _insert(newRow: any): void {
    this.waterRightVersionPurposesService
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          if (dto.calcVolWarnings) {
            const warnings = dto.calcVolWarnings
              .map((w) => w.warning)
              .join(' \n - ');
            const confirmationDialog = this.dialog.open(
              ConfirmationDialogComponent,
              {
                data: {
                  title: 'Warning',
                  message: `- ${warnings}`,
                  confirmButtonName: 'Continue',
                  hideCancelButton: true,
                },
              }
            );
            confirmationDialog.afterClosed().subscribe(() => {
              let messages = ['Record successfully added.'];
              if (!!dto?.messages) {
                messages = [...dto.messages, ...messages];
              }
              this.reditectToNewPurpose(dto.purposeId);
              this.snackBar.open(messages.join('\n'));
              this._get();
            });
          } else {
            let messages = ['Record successfully added.'];
            if (!!dto?.messages) {
              messages = [...dto.messages, ...messages];
            }
            this.reditectToNewPurpose(dto.purposeId);
            this.snackBar.open(messages.join('\n'));
            this._get();
          }
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

  /*
   * Display the Insert dialog and, if data is returned, call the insert function
   */
  protected _displayInsertDialog(data: any): void {
    this.setValidators();
    // Open the dialog
    const dialogRef = this.dialog.open(PurposeInsertOrEditDialogComponent, {
      width: 'auto',
      data: {
        title: 'Add New Purpose Record',
        mode: DataManagementDialogModes.Insert,
        columns: this.dialogFormColumns,
        placeOfUseColumns: this.placeOfUseColumns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        periodColumns: this.periodColumns,
        values: {
          ...data,
          versionNumber: this.data.versionNumber,
          waterRightTypeCode: this.data.waterRightTypeCode,
          waterRightStatusCode: this.data.waterRightStatusCode,
          has650Application: this.data?.applicationTypeCodes?.includes('650'),
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result: Purpose) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  // Handle the onEdit event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.route.snapshot.params.versionId),
      this.data.isDecreed,
      this.data.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }
}
