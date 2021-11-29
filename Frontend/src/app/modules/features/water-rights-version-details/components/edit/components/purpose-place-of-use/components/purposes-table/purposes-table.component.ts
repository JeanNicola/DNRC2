/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { PurposeInsertOrEditDialogComponent } from 'src/app/modules/features/purposes/components/edit/components/edit-header/components/purpose-insert-dialog/purpose-insert-or-edit-dialog.component';
import { PurposeDropdownsService } from 'src/app/modules/features/purposes/components/edit/components/edit-header/services/purpose-dropdowns.service';
import { EditPurposeHeaderComponent } from 'src/app/modules/features/purposes/components/edit/components/edit-header/edit-header.component';
import { WaterRightVersionPurposesService } from 'src/app/modules/features/purposes/components/edit/components/edit-header/services/water-right-version-purposes.service';
import { PurposesService } from 'src/app/modules/features/purposes/components/search/services/purposes.service';
import { Purpose } from 'src/app/modules/features/purposes/interfaces/purpose.interface';
import { ConfirmationDialogComponent } from 'src/app/modules/shared/components/dialogs/confirmation-dialog/confirmation-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { PodDropdownService } from '../../../point-of-diversion/services/pod-dropdown.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-purposes-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
})
export class PurposesTableComponent extends BaseCodeTableComponent {
  @Output() dataChanged = new EventEmitter<void>();
  @Input() waterRightStatusCode = null;
  @Input() waterRightTypeCode = null;
  @Input() waterRightNumber = null;
  @Input() waterRightId = null;
  @Input() versionNumber = null;
  @Input() basin = null;
  @Input() ext = null;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() applicationTypeCodes = null;
  @Input() canEdit = false;

  constructor(
    public service: PurposesService,
    public waterRightVersionPurposesService: WaterRightVersionPurposesService,
    public dropdownService: PurposeDropdownsService,
    public podDropdownService: PodDropdownService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public title = '';
  public primarySortColumn = 'completePurposeCode';
  public sortDirection = 'asc';
  public dblClickableRow = true;
  public searchable = false;
  public hideEdit = true;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completePurposeCode',
      title: 'Purpose',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'purposeVolume',
      title: 'Volume',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'clarification',
      title: 'Purpose Clarification',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'climaticCodeDescription',
      title: 'Climatic Area',
      type: FormFieldTypeEnum.Input,
    },
  ];

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
      columnId: 'periodBegin1',
      title: 'Begin Date',
      type: FormFieldTypeEnum.Input,
      validators: [Validators.required],
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'endDate1',
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

  protected initFunction() {
    if (
      this.applicationTypeCodes?.includes('650') &&
      !this._getDialogColumns('leaseYear', this.periodColumns)
    ) {
      this.periodColumns = [
        ...this.periodColumns,
        {
          columnId: 'leaseYear',
          title: 'Lease Year',
          type: FormFieldTypeEnum.Select,
        },
      ];

      this._getDialogColumns('leaseYear', this.periodColumns).selectArr =
        this.dropdownService.leaseYearValues;
    }

    this.queryParameters = {
      sortDirection: '',
      sortColumn: 'purposeDescription',
      pageSize: 25,
      pageNumber: 1,
      filters: {
        waterRightType: this.waterRightTypeCode,
        waterRightNumber: this.waterRightNumber,
        basin: this.basin,
        version: this.versionNumber,
        purposeSearchType: 'WATERRIGHTVERSION',
      },
    };
    if (this.ext) {
      this.queryParameters.filters.ext = this.ext;
    }
    this._get();
  }

  private redirectToPurposeEditScreen(
    waterRightId: number,
    versionId: number,
    purposeId: number
  ) {
    void this.router.navigate([
      'wris',
      'water-rights',
      waterRightId,
      'versions',
      versionId,
      'purposes',
      purposeId,
    ]);
  }

  public onRowDoubleClick(data: any): void {
    this.redirectToPurposeEditScreen(
      data.waterRightId,
      data.versionId,
      data.purposeId
    );
  }

  private setValidators(): void {
    this._getDialogColumns('purposeOrigin', this.dialogFormColumns).validators =
      [
        Validators.required,
        EditPurposeHeaderComponent.originValidator(
          this.waterRightTypeCode,
          this.waterRightStatusCode
        ),
      ];
    this._getDialogColumns('purposeCode', this.dialogFormColumns).validators = [
      Validators.required,
      EditPurposeHeaderComponent.purposeTypeValidator(
        this.waterRightTypeCode,
        +this.versionNumber,
        this.applicationTypeCodes
      ),
    ];
    this._getDialogColumns('household', this.dialogFormColumns).validators = [
      WRISValidators.isNumber(4, 0),
      EditPurposeHeaderComponent.householdValidator(this.waterRightTypeCode),
    ];
    this._getDialogColumns('elementOrigin', this.placeOfUseColumns).validators =
      [
        Validators.required,
        EditPurposeHeaderComponent.originValidator(
          this.waterRightTypeCode,
          this.waterRightStatusCode
        ),
      ];
    this._getDialogColumns('elementOrigin', this.periodColumns).validators = [
      Validators.required,
      EditPurposeHeaderComponent.originValidator(
        this.waterRightTypeCode,
        this.waterRightStatusCode
      ),
    ];
  }

  protected _buildInsertIdArray(dto: any): string[] {
    return [this.waterRightId, this.versionNumber];
  }

  /*
   * Delete the data using the data service
   */
  protected _delete(row: number): void {
    this._getDeleteService()
      .delete(...this._buildDeleteIdArray(row))
      .subscribe(
        () => {
          this._get();
          this.dataChanged.emit(null);
          this.snackBar.open('Record successfully deleted.');
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot delete record. ';
          message += errorBody.userMessage || ErrorMessageEnum.DELETE;
          this.snackBar.open(message);
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
              this.dataChanged.emit(null);
              this.snackBar.open(messages.join('\n'));
              this._get();
            });
          } else {
            let messages = ['Record successfully added.'];
            if (!!dto?.messages) {
              messages = [...dto.messages, ...messages];
            }
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

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
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
          versionNumber: this.versionNumber,
          waterRightTypeCode: this.waterRightTypeCode,
          waterRightStatusCode: this.waterRightStatusCode,
          has650Application: this.applicationTypeCodes?.includes('650'),
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

  private _getDialogColumns(
    columnId: string,
    columns
  ): ColumnDefinitionInterface {
    return columns.find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }
  /*
   * Display the Delete dialog
   */
  protected _displayDeleteDialog(row: number): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: {
        message:
          'Deleting a Purpose will remove all linked <strong>Places Of Use</strong>, <strong>Periods</strong>, <strong>Retired Places Of Use</strong> and <strong>Examinations</strong>. Do you want to continue?',
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
    });
  }

  protected populateDropdowns(): void {
    // Purpose Types
    this._getDialogColumns('purposeCode', this.dialogFormColumns).selectArr =
      this.dropdownService.purposeCodes;

    // Origins
    this._getDialogColumns('purposeOrigin', this.dialogFormColumns).selectArr =
      this.dropdownService.ownerOrigins;

    this._getDialogColumns('elementOrigin', this.placeOfUseColumns).selectArr =
      this.dropdownService.ownerOrigins;
    this._getDialogColumns('elementOrigin', this.periodColumns).selectArr =
      this.dropdownService.ownerOrigins;

    // YES/NO
    // Modified By This Change
    this._getDialogColumns(
      'modifiedByThisChange',
      this.dialogFormColumns
    ).selectArr = this.dropdownService.yesNoValues;

    if (+this.versionNumber !== 1) {
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

    // Crop Rotation
    this._getDialogColumns('rotation', this.dialogFormColumns).selectArr =
      this.dropdownService.yesNoValues;

    // Irrigation Types
    this._getDialogColumns('irrigationCode', this.dialogFormColumns).selectArr =
      this.dropdownService.irrigationTypes;

    // Climatic Areas
    this._getDialogColumns('climaticCode', this.dialogFormColumns).selectArr =
      this.dropdownService.climaticAreas;

    // Populate Legal Land fields
    this._getDialogColumns(
      'countyId',
      this.secondLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.counties;

    this._getDialogColumns(
      'description320',
      this.firstLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.aliquots;
    this._getDialogColumns(
      'description160',
      this.firstLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.aliquots;
    this._getDialogColumns(
      'description80',
      this.firstLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.aliquots;
    this._getDialogColumns(
      'description40',
      this.firstLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.aliquots;

    this._getDialogColumns(
      'townshipDirection',
      this.secondLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.townshipDirections;

    this._getDialogColumns(
      'rangeDirection',
      this.secondLegalLandDescriptionColumns
    ).selectArr = this.podDropdownService.rangeDirections;
  }

  /*
   * setPermissions
   *
   * Gets the permissions from the REST endpoint service stores them for later use in the component
   */
  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST:
        this.endpointService.canPOST(
          this.waterRightVersionPurposesService.url
        ) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].purposeId];
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
