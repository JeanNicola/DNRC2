/* eslint-disable max-len */
import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { EditMessageComponent } from 'src/app/modules/shared/components/dialogs/edit-message/edit-message.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DeleteDialogComponent } from 'src/app/modules/shared/components/dialogs/delete-dialog/delete-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { BaseDataService } from 'src/app/modules/shared/services/base-data.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { InsertUpdateAcreageComponent } from '../../../../../create/components/insert-update-acreage/insert-update-acreage.component';
import { CopyPousToRetiredService } from '../../services/copy-pous-to-retired.service';
import { CopyPodsService } from '../../services/copy-pods.service';
import { PlacesOfUseService } from '../../services/places-of-use.service';
import { PurposeDropdownsService } from '../../../edit-header/services/purpose-dropdowns.service';
import { takeUntil } from 'rxjs/operators';
import { PodDropdownService } from 'src/app/modules/features/water-rights-version-details/components/edit/components/point-of-diversion/services/pod-dropdown.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

enum validCountiesFor645Application {
  BEAVERHEAD = 5,
  FLATHEAD = 19,
}

@Component({
  selector: 'app-place-of-use',
  templateUrl: './place-of-use.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './place-of-use.component.scss',
  ],
  providers: [
    CopyPousToRetiredService,
    CopyPodsService,
    { provide: BaseDataService, useClass: PlacesOfUseService },
  ],
})
export class PlaceOfUseComponent
  extends BaseCodeTableComponent
  implements OnInit, OnDestroy
{
  constructor(
    public service: BaseDataService,
    public copyPodsService: CopyPodsService,
    public dropdownService: PurposeDropdownsService,
    public podDropdownService: PodDropdownService,
    public copyPousToRetiredService: CopyPousToRetiredService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public route: ActivatedRoute
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Output() dataChanged: EventEmitter<void> = new EventEmitter<void>();
  @Output() selectPlaceOfUse = new EventEmitter<any>();
  @Input() purposeId = null;
  @Input() waterRightTypeCode = null;
  @Input() waterRightStatusCode = null;
  @Input() versionNumber = null;
  @Input() has645Application = null;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() canEdit = false;
  @Input() reloadDataObservable: Observable<any> = null;

  get idArray(): string[] {
    return super.idArray;
  }

  private fetchSubdivisions = true;
  protected executingCopy = false;

  public primarySortColumn = 'placeId';
  public searchable = false;
  public isInMain = false;
  public clickableRow = true;
  public dblClickableRow = true;
  public highlightOneRow = true;
  public highlightFirstRowOnInit = true;
  public title = '';
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: '',
    sortColumn: 'placeId',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  // Legal Land Data
  private legalLandDescriptionColumns = [
    'governmentLot',
    'description320',
    'description160',
    'description80',
    'description40',
    'section',
    'township',
    'townshipDirection',
    'range',
    'rangeDirection',
  ];

  protected firstLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'governmentLot',
      title: 'Govt Lot',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 160,
      validators: [
        WRISValidators.isNumber(3, 0),
        WRISValidators.updateValidityOfOtherFields(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'governmentLot'
          )
        ),
      ],
    },
    {
      columnId: 'description40',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      validators: [
        WRISValidators.updateValidityOfOtherFields(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'description40'
          )
        ),
      ],
      formWidth: 100,
    },
    {
      columnId: 'description80',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
      validators: [
        WRISValidators.updateValidityOfOtherFields(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'description80'
          )
        ),
      ],
    },
    {
      columnId: 'description160',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
      validators: [
        WRISValidators.updateValidityOfOtherFields(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'description160'
          )
        ),
      ],
    },
    {
      columnId: 'description320',
      title: '1/4',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 100,
      validators: [
        WRISValidators.updateValidityOfOtherFields(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'description320'
          )
        ),
      ],
    },
  ];

  protected secondLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
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

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'placeId',
      title: 'Parcel ID',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'acreage',
      title: 'Acreage',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
    {
      columnId: 'elementOrigin',
      title: 'Place Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'elementOriginDescription',
      title: 'Place Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'completeLegalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'modifiedByThisChange',
      title: 'Modified By This Change',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
    },
    {
      columnId: 'modifiedByThisChangeDescription',
      title: 'Modified By This Change',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
    },
  ];

  public ngOnInit() {
    if (+this.route.snapshot.params.versionId === 1) {
      this._getColumn('modifiedByThisChangeDescription').displayInTable = false;
      this._getColumn('modifiedByThisChange').displayInInsert = false;
      this._getColumn('modifiedByThisChange').displayInEdit = false;
    }

    this.afterInit();
  }

  protected _getColumn(columnId: string) {
    return [
      ...this.columns,
      ...this.firstLegalLandDescriptionColumns,
      ...this.secondLegalLandDescriptionColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }

  public rowClick(data: any): void {
    this.selectPlaceOfUse.emit(data);
  }

  // Handle the onInsert event
  public onInsert(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, null)
    );
  }

  // Handle the onEdit event
  public onEdit(updatedData: any, index: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
    );
  }

  // Handle the onDelete event
  public onDelete(row: number): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayDeleteDialog.bind(this, row)
    );
  }

  // Handle the onCopy event
  public onCopy(data: any): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayInsertDialog.bind(this, data)
    );
  }

  // Handle the onCopy event
  public copyPODS(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.versionNumber),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._doCopyPODs.bind(this)
    );
  }

  private _doCopyPODs() {
    const confirmDialog = this.dialog.open(EditMessageComponent, {
      width: '500px',
      data: {
        title: 'Copy POD Data',
        message:
          'This will copy all POD data to Place Of Use. Do you want to continue?',
      },
    });

    confirmDialog.afterClosed().subscribe((r) => {
      if (r === 'continue') {
        this.copyPodsService.insert(null, this.purposeId).subscribe(
          () => {
            this.executingCopy = true;
            this._get();
          },
          (err: HttpErrorResponse) => {
            const errorBody = err.error as ErrorBodyInterface;
            const message = errorBody.userMessage;
            this.snackBar.open(message);
          }
        );
      }
    });
  }

  protected afterInit(): void {
    if (this.reloadDataObservable) {
      this.reloadDataObservable
        .pipe(takeUntil(this.unsubscribe))
        .subscribe(() => {
          this.fetchSubdivisions = false;
          this._get();
        });
    }

    super.ngOnInit();

    this._idArray = [this.purposeId];
    this._get();
  }

  protected _getHelperFunction(data: any): any {
    if (data.get?.results?.length && this.fetchSubdivisions) {
      this.selectPlaceOfUse.emit(data.get.results[0]);
      if (this.rows) {
        this.dataChanged.emit(null);
      }
    } else if (!data.get?.results?.length) {
      this.selectPlaceOfUse.emit(null);
    }

    if (this.executingCopy && !data.get?.results?.length) {
      this.snackBar.open('No Place Of Diversion records found to copy.');
    }

    this.executingCopy = false;

    setTimeout(() => {
      this.fetchSubdivisions = true;
    });
    return data.get;
  }

  protected _getColumnFromDifferentSource(
    columnId: string,
    columns
  ): ColumnDefinitionInterface {
    return columns.find(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );
  }

  protected populateDropdowns(): void {
    // Get element origins
    let selectArray = this.dropdownService.ownerOrigins;
    if (this.waterRightTypeCode !== 'CMPT') {
      selectArray = this.dropdownService.ownerOrigins.filter(
        (option) => option.value !== 'CMPT'
      );
    } else if (
      this.waterRightTypeCode === 'CMPT' &&
      this.waterRightStatusCode !== 'N/A'
    ) {
      selectArray = this.dropdownService.ownerOrigins.filter(
        (option) => option.value === 'CMPT'
      );
    } else {
      selectArray = this.dropdownService.ownerOrigins;
    }
    this._getColumn('elementOrigin').selectArr = selectArray;

    // Get modified by this change
    this._getColumn('modifiedByThisChange').selectArr =
      this.dropdownService.yesNoValues;

    // Get counties
    if (
      this.waterRightTypeCode === 'PRPM' &&
      +this.route.snapshot.params.versionId === 1 &&
      this.has645Application
    ) {
      selectArray = this.podDropdownService.counties.filter((option: any) =>
        [
          validCountiesFor645Application.FLATHEAD,
          validCountiesFor645Application.BEAVERHEAD,
        ].includes(option.value)
      );
    } else {
      selectArray = this.podDropdownService.counties;
    }
    this._getColumn('countyId').selectArr = selectArray;

    // Get remaining select arrays
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
  }

  protected createPlaceOfUseDto(result) {
    return {
      acreage: result.acreage,
      elementOrigin: result.elementOrigin,
      legalLand: {
        description40: result.description40,
        description80: result.description80,
        description160: result.description160,
        description320: result.description320,
        governmentLot: result.governmentLot,
        section: result.section,
        township: result.township,
        townshipDirection: result.townshipDirection,
        range: result.range,
        rangeDirection: result.rangeDirection,
        countyId: result.countyId,
      },
      modifiedByThisChange: result.modifiedByThisChange,
    };
  }

  protected _insert(newRow: any): void {
    this.service
      .insert(this._buildInsertDto(newRow), ...this._buildInsertIdArray(newRow))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully added.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot insert new record. ';
          message += errorBody.userMessage || ErrorMessageEnum.POST;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayInsertDialog({ ...newRow.legalLand, ...newRow });
        }
      );
  }

  protected _displayInsertDialog(data): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: 'Add New Place Of Use Record',
        mode: DataManagementDialogModes.Insert,
        placeOfUseColumns: this.columns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        values: {
          ...data,
          versionNumber: +this.route.snapshot.params.versionId,
        },
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._insert(this.createPlaceOfUseDto(result));
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.placeId];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].placeId];
  }

  protected _displayEditDialog(data): void {
    // Open the dialog
    const dialogRef = this.dialog.open(InsertUpdateAcreageComponent, {
      data: {
        title: 'Update Place Of Use Record',
        mode: DataManagementDialogModes.Update,
        placeOfUseColumns: this.columns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        values: {
          ...data,
          versionNumber: +this.route.snapshot.params.versionId,
        },
      },
    });
    // Get the input data and peform the update
    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this.createPlaceOfUseDto(result), data);
      }
    });
  }

  private createDeleteMessage(placeOfUse): string {
    let message = 'This parcel is linked to ';
    let children = '';
    if (placeOfUse.hasExaminations) {
      children += '<strong>examinations</strong>';
    }
    if (placeOfUse.hasSubdivisions) {
      if (placeOfUse.hasExaminations) {
        children += ' and ';
      }
      children += '<strong>subdivisions</strong>';
    }
    message +=
      children +
      `. Deleting it will delete the link to ${children} records, are you sure you want to continue?`;
    return message;
  }

  /*
   * Display the Delete dialog
   */
  protected _displayDeleteDialog(row: number): void {
    let message = null;

    if (this.rows[row].hasExaminations || this.rows[row].hasSubdivisions) {
      message = this.createDeleteMessage(this.rows[row]);
    }

    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '500px',
      data: { message },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'delete') {
        this._delete(row);
      }
    });
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPOST, canDELETE, and canPUT values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
  }
}
