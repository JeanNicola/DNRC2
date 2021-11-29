import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { ReplaySubject } from 'rxjs';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import {
  FlowRateUnitsService,
  FlowRateUnitType,
} from 'src/app/modules/shared/services/flow-rate-units.service';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { MeasurementsService } from '../../services/measurements.service';

@Component({
  selector: 'app-measurements-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './measurements-table.component.scss',
  ],
  providers: [MeasurementsService, FlowRateUnitsService],
})
export class MeasurementsTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: MeasurementsService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public flowRateUnitsService: FlowRateUnitsService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
        this.rows = null;
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return this._idArray;
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() operatingAuthorityDate: string;

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'year',
      title: 'Year',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'flowRate',
      title: 'Highest Flow Rate',
      type: FormFieldTypeEnum.Input,
      validators: [
        WRISValidators.requireOtherFieldIfNonNull('unit'),
        WRISValidators.isNumber(8, 2),
      ],
    },
    {
      columnId: 'unit',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Select,
      validators: [WRISValidators.requireOtherFieldIfNonNull('flowRate')],
    },
    {
      columnId: 'volume',
      title: 'Total Volume Used',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(8, 2)],
    },
  ];

  public title = 'Measurements';
  public searchable = false;
  public validators = [
    WRISValidators.requireOneOtherField(
      'year',
      'Enter either the Highest Flow Rate or Total Volume Used',
      'flowRate',
      'volume'
    ),
  ];

  public initFunction(): void {
    this._getColumn('year').validators = [
      WRISValidators.minimumYear(moment(this.operatingAuthorityDate).year()),
      WRISValidators.maximumYear(moment().year()),
      Validators.required,
    ];
  }

  protected populateDropdowns(): void {
    this.observables.flowRateUnits = new ReplaySubject(1);
    this.flowRateUnitsService
      .get(this.queryParameters)
      .subscribe((units: { results: FlowRateUnitType[] }) => {
        units.results.unshift({ description: '', value: null });
        this._getColumn('unit').selectArr = units.results.map(
          (type: FlowRateUnitType) => ({
            name: type.description,
            value: type.value,
          })
        );
        this.observables.flowRateUnits.next(units);
        this.observables.flowRateUnits.complete();
      });
    this.observables.flowRateUnits = new ReplaySubject(1);
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
  public onEdit(updatedData: any, index: number): void {
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

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.id];
  }

  protected _buildDeleteIdArray(rowNumber: number): string[] {
    return [...this.idArray, this.rows[rowNumber].id];
  }

  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
