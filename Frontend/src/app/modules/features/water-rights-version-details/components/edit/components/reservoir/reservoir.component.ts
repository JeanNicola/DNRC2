import { Component, Input, OnDestroy } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { Reference } from 'src/app/modules/shared/interfaces/reference.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { ReservoirDialogComponent } from './components/reservoir-dialog/reservoir-dialog.component';
import { ReservoirOriginsService } from './services/reservoir-origins.service';
import {
  PodOption,
  VersionAllPodsService,
} from './services/version-all-pods.service';
import { VersionReservoirService } from './services/version-reservoirs.service';
import { ReservoirTypesService } from './services/reservoir-types.service';
import { takeUntil } from 'rxjs/operators';
import { PodDropdownService } from '../point-of-diversion/services/pod-dropdown.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';

@Component({
  selector: 'app-reservoir',
  templateUrl:
    '../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    './reservoir.component.scss',
    '../../../../../../shared/components/templates/code-table/code-table.template.scss',
  ],
  providers: [
    VersionReservoirService,
    VersionAllPodsService,
    ReservoirOriginsService,
    ReservoirTypesService,
  ],
})
export class ReservoirComponent
  extends BaseCodeTableComponent
  implements OnDestroy
{
  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() podsUpdated = null;
  constructor(
    public service: VersionReservoirService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public podService: VersionAllPodsService,
    public dropdownService: PodDropdownService,
    public originService: ReservoirOriginsService,
    public reservoirTypeService: ReservoirTypesService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public legalLandDescriptionColumns = [
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

  public firstColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'reservoirId',
      title: 'Reservoir Id',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInTable: false,
      displayInInsert: false,
    },
    {
      columnId: 'reservoirName',
      title: 'Reservoir / Pit Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'podId',
      title: 'POD ID',
      type: FormFieldTypeEnum.Select,
      formWidth: 120,
      displayInTable: false,
    },
    {
      columnId: 'podNumber',
      title: 'POD ID',
      type: FormFieldTypeEnum.Select,
      displayInEdit: false,
      displayInInsert: false,
    },
    {
      columnId: 'reservoirOriginCode',
      title: 'Reservoir Origin',
      type: FormFieldTypeEnum.Select,
      formWidth: 240,
      displayInTable: false,
      validators: [Validators.required],
    },
    {
      columnId: 'reservoirOriginDescription',
      title: 'Reservoir Origin',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
      displayInInsert: false,
      width: 200,
    },
  ];

  public secondColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'reservoirTypeCode',
      title: 'On or Off Stream',
      type: FormFieldTypeEnum.Select,
      validators: [Validators.required],
      displayInTable: false,
    },
    {
      columnId: 'reservoirTypeDescription',
      title: 'On or Off Stream',
      type: FormFieldTypeEnum.Input,
      displayInInsert: false,
      displayInEdit: false,
    },
  ];

  public firstReservoirCharacteristicColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'currentCapacity',
      title: 'Current Capacity',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      formWidth: 160,
      noSort: true,
    },
    {
      columnId: 'enlargedCapacity',
      title: 'Enlarged Capacity',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      formWidth: 170,
      noSort: true,
    },
    {
      columnId: 'maxDepth',
      title: 'Max Depth',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      formWidth: 140,
      noSort: true,
    },
  ];

  public secondReservoirCharacteristicColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'damHeight',
      title: 'Dam Height',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(4, 2)],
      formWidth: 140,
      noSort: true,
    },
    {
      columnId: 'surfaceArea',
      title: 'Surface Area',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
      formWidth: 140,
      noSort: true,
    },
    {
      columnId: 'elevation',
      title: 'Elevation',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(4, 1)],
      formWidth: 120,
      noSort: true,
    },
  ];

  public firstLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'completeLegalLandDescription',
      title: 'Legal Land Description',
      type: FormFieldTypeEnum.Input,
      width: 300,
      displayInEdit: false,
      displayInInsert: false,
    },
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
    {
      columnId: 'section',
      title: 'Sec',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 110,
      validators: [
        WRISValidators.isNumber(2, 0),
        WRISValidators.requireOtherFieldsIfAnyNonNull(
          ...this.legalLandDescriptionColumns.filter((c) => c !== 'section')
        ),
      ],
    },
  ];

  public secondLegalLandDescriptionColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'township',
      title: 'Twp',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 120,
      validators: [
        WRISValidators.isNumber(3, 1),
        WRISValidators.requireOtherFieldsIfAnyNonNull(
          ...this.legalLandDescriptionColumns.filter((c) => c !== 'township')
        ),
      ],
    },
    {
      columnId: 'townshipDirection',
      title: 'N/S',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      formWidth: 80,
      validators: [
        WRISValidators.requireOtherFieldsIfAnyNonNull(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'townshipDirection'
          )
        ),
      ],
    },
    {
      columnId: 'range',
      title: 'Rge',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      formWidth: 100,
      validators: [
        WRISValidators.isNumber(3, 1),
        WRISValidators.requireOtherFieldsIfAnyNonNull(
          ...this.legalLandDescriptionColumns.filter((c) => c !== 'range')
        ),
      ],
    },
    {
      columnId: 'rangeDirection',
      title: 'E/W',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      formWidth: 80,
      validators: [
        WRISValidators.requireOtherFieldsIfAnyNonNull(
          ...this.legalLandDescriptionColumns.filter(
            (c) => c !== 'rangeDirection'
          )
        ),
      ],
    },
    {
      columnId: 'countyId',
      title: 'County',
      type: FormFieldTypeEnum.Autocomplete,
      displayInTable: false,
      formWidth: 230,
      validators: [Validators.required, WRISValidators.isNumber(10, 0)],
    },
    {
      columnId: 'changed',
      title: 'Modified in this Change',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  public columns: ColumnDefinitionInterface[] = [
    ...this.firstColumns,
    ...this.secondColumns,
    ...this.firstReservoirCharacteristicColumns,
    ...this.secondReservoirCharacteristicColumns,
    ...this.firstLegalLandDescriptionColumns,
    ...this.secondLegalLandDescriptionColumns,
  ];
  public title = '';
  public searchable = false;
  private podOptions: any[];

  public sortDirection = 'asc';
  public primarySortColumn = 'reservoirName';

  public initFunction(): void {
    this._get();
    this.podsUpdated.pipe(takeUntil(this.unsubscribe)).subscribe(() => {
      this._updatePodIds();
      this._get();
    });
  }

  private _updatePodIds(): void {
    this.observables.pod = new ReplaySubject(1);
    this.podService
      .get(this.queryParameters, ...this.idArray)
      .subscribe((pods: { results: PodOption[] }) => {
        this._getColumn('podId').selectArr = pods.results.map(
          (pod: PodOption) => ({
            name: String(pod.podNumber),
            value: pod.podId,
          })
        );
        this.podOptions = pods.results;
        this.observables.pod.next(pods);
        this.observables.pod.complete();
      });
  }

  protected setInitialFocus(): void {}

  public populateDropdowns(): void {
    this._updatePodIds();

    this._getColumn('countyId').selectArr = this.dropdownService.counties;
    this._getColumn('description320').selectArr = this.dropdownService.aliquots;
    this._getColumn('description160').selectArr = this.dropdownService.aliquots;
    this._getColumn('description80').selectArr = this.dropdownService.aliquots;
    this._getColumn('description40').selectArr = this.dropdownService.aliquots;
    this._getColumn('townshipDirection').selectArr =
      this.dropdownService.townshipDirections;
    this._getColumn('rangeDirection').selectArr =
      this.dropdownService.rangeDirections;

    this.observables.origins = new ReplaySubject(1);
    this.originService
      .get(this.queryParameters)
      .subscribe((origins: { results: Reference[] }) => {
        this._getColumn('reservoirOriginCode').selectArr = origins.results.map(
          (origin: Reference) => ({
            name: origin.description,
            value: origin.value,
          })
        );
        this.observables.origins.next(origins);
        this.observables.origins.complete();
      });

    this.observables.types = new ReplaySubject(1);
    this.reservoirTypeService
      .get(this.queryParameters)
      .subscribe((types: { results: Reference[] }) => {
        const selectArr = types.results.map((type: Reference) => ({
          name: type.description,
          value: type.value,
        }));
        this._getColumn('reservoirTypeCode').selectArr = selectArr;
        this.observables.types.next(types);
        this.observables.types.complete();
      });
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT, canPOST, canDELETE values
    this.permissions = {
      ...this.permissions,
      canPOST: this.endpointService.canPOST(this.service.url) && this.canEdit,
      canDELETE:
        this.endpointService.canDELETE(this.service.url) && this.canEdit,
      canPUT: this.endpointService.canPUT(this.service.url) && this.canEdit,
    };
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

  // Handle the onEdit event
  public onEdit(updatedData: any): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, updatedData)
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

  protected _buildEditIdArray(dto: any, originalData: any): string[] {
    return [...this.idArray, originalData.reservoirId];
  }

  protected _buildDeleteIdArray(rowNumber: number) {
    return [...this.idArray, this.rows[rowNumber].reservoirId];
  }

  protected _displayInsertDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ReservoirDialogComponent, {
      width: null,
      data: {
        title: 'Add New Reservoir',
        columns: this.columns,
        firstColumns: this.firstColumns,
        secondColumns: this.secondColumns,
        firstReservoirCharacteristicColumns:
          this.firstReservoirCharacteristicColumns,
        secondReservoirCharacteristicColumns:
          this.secondReservoirCharacteristicColumns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        values: {
          reservoirOriginCode: 'ISSU',
          reservoirTypeCode: 'ON',
          changed: false,
          ...data,
        },
        legalLandDescriptionFields: this.legalLandDescriptionColumns,
        podOptions: this.podOptions,
        type: DataManagementDialogModes.Insert,
      },
    });

    // Get the input data and peform the insert
    dialogRef.afterClosed().subscribe((result: any[]) => {
      if (result !== null && result !== undefined) {
        this._insert(result);
      } else {
        this.firstInsert.focus();
      }
    });
  }

  protected _displayEditDialog(data: any): void {
    // Open the dialog
    const dialogRef = this.dialog.open(ReservoirDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: 'Update Reservoir',
        columns: this.columns,
        values: data,
        firstColumns: this.firstColumns,
        secondColumns: this.secondColumns,
        firstReservoirCharacteristicColumns:
          this.firstReservoirCharacteristicColumns,
        secondReservoirCharacteristicColumns:
          this.secondReservoirCharacteristicColumns,
        firstLegalLandDescriptionColumns: this.firstLegalLandDescriptionColumns,
        secondLegalLandDescriptionColumns:
          this.secondLegalLandDescriptionColumns,
        legalLandDescriptionFields: this.legalLandDescriptionColumns,
        podOptions: this.podOptions,
        type: DataManagementDialogModes.Update,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }
}
