import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import {
  FlowRateUnitsService,
  FlowRateUnitType,
} from 'src/app/modules/shared/services/flow-rate-units.service';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { WaterRightsSummaryService } from '../../services/water-rights-summary.service';

@Component({
  selector: 'app-water-rights-summary',
  // templateUrl: './water-rights-summary.component.html',
  templateUrl:
    '../../../../../../../../shared/components/templates/data-row/data-row.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './water-rights-summary.component.scss',
  ],
  providers: [WaterRightsSummaryService, FlowRateUnitsService],
})
export class WaterRightsSummaryComponent extends DataRowComponent {
  private _appTypeCode: string;
  @Input() set appTypeCode(str: string) {
    this._appTypeCode = str;
    this._setNonFiledProperties(this.data);
  }
  @Input() waterRightChange: Observable<DataQueryParametersInterface>;
  private unsubscribe = new Subject();

  columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'maxFlowRate',
      title: 'Flow Rate (Max)',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [
        WRISValidators.isNumber(10, 2),
        Validators.min(0),
        WRISValidators.requireOtherFieldIfNonNull('flowRateUnit'),
      ],
    },
    {
      columnId: 'flowRateUnit',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [WRISValidators.requireOtherFieldIfNonNull('maxFlowRate')],
    },
    // This is concantenated maxFlowRate + ' ' + flowRateUnit for display purposes
    {
      columnId: 'flowRate',
      title: 'Flow Rate (Max. & Unit)',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'volume',
      title: 'Volume (Max)',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(10, 2), Validators.min(0)],
    },
    {
      columnId: 'acres',
      title: 'Acres (Max)',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(10, 2), Validators.min(0)],
    },

    {
      columnId: 'nonFiledWaterProject',
      title: 'Non-Filed Water Project',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  constructor(
    public service: WaterRightsSummaryService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService,
    public flowRateUnitsService: FlowRateUnitsService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  public ngDestroy(): void {
    super.ngOnDestroy();
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  initFunction(): void {
    this._get();
    this.waterRightChange
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((dataQueryParameters) => {
        if (dataQueryParameters) {
          this.queryParameters = dataQueryParameters;
        }
        this._get();
      });
  }

  // Set nonFiledWaterProject property to be editable or not
  protected _getHelperFunction(data: any): { [key: string]: any } {
    this._setNonFiledProperties(data.get);
    const newData = { ...data.get };
    if (newData.maxFlowRate && newData.flowRateUnit) {
      newData.flowRate = newData.maxFlowRate + ' ' + newData.flowRateUnit;
    } else {
      newData.flowRate = null;
    }
    return { ...newData };
  }

  // This function shows nonFiledWaterProject for certain appTypeCodes
  // It is run any time the appTypeCode changes or whenever new summary
  // data is received. Also disables edit entirely for certain type codes.
  private _setNonFiledProperties(data: any): void {
    let nonFiledEditable = false;
    if (data) {
      nonFiledEditable = data.canPressNonFiledWaterProject as boolean;
    }
    if (
      ['105', '604', '606', '626', '627', '634', '644', '650'].includes(
        this._appTypeCode
      )
    ) {
      this.disableEdit = false;
    } else {
      if (!nonFiledEditable) {
        this.disableEdit = true;
      }
      this._getColumn('maxFlowRate').editable = false;
      this._getColumn('flowRateUnit').editable = false;
      this._getColumn('volume').editable = false;
      this._getColumn('acres').editable = false;
    }
    if (['606', '634'].includes(this._appTypeCode)) {
      this._getColumn('nonFiledWaterProject').displayInTable = true;
      this._getColumn('nonFiledWaterProject').displayInEdit = true;
    } else {
      this._getColumn('nonFiledWaterProject').displayInTable = false;
      this._getColumn('nonFiledWaterProject').displayInEdit = false;
    }
    this._getColumn('nonFiledWaterProject').editable = nonFiledEditable;
    if (!nonFiledEditable) {
      this._getColumn('nonFiledWaterProject').displayInEdit = false;
    }
  }

  // Used to override canPUT without an id
  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0),
    };
  }

  public populateDropdowns(): void {
    // create a separate observable that only emits the one value
    // this way, the getPrograms http request and the selectArr
    // population only happens once
    this.observables.flowRateUnits = new ReplaySubject(1);
    this.flowRateUnitsService
      .get(this.queryParameters)
      .subscribe((flowRateUnits: { results: FlowRateUnitType[] }) => {
        this._getColumn('flowRateUnit').selectArr = flowRateUnits.results.map(
          (flowRateUnit: FlowRateUnitType) => ({
            name: flowRateUnit.description,
            value: flowRateUnit.value,
          })
        );
        this._getColumn('flowRateUnit').selectArr.unshift({
          name: '',
          value: null,
        });
        this.observables.flowRateUnits.next(flowRateUnits);
        this.observables.flowRateUnits.complete();
      });
  }
}
