/* eslint-disable max-len */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { MoreInfoDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/more-info-dialog/more-info-dialog.component';
import { DataRowComponent } from 'src/app/modules/shared/components/templates/data-row/data-row.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { PodWellDataUpdateService } from '../../services/pod-well-data-update.service';

@Component({
  selector: 'app-pod-well-data',
  templateUrl: './pod-well-data.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/data-row/data-row.component.scss',
    './pod-well-data.component.scss',
  ],
  providers: [PodWellDataUpdateService],
})
export class PodWellDataComponent extends DataRowComponent {
  constructor(
    public service: PodWellDataUpdateService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;
  @Input() set values(value: any) {
    if (value === null) {
      this.dataMessage = 'No data found';
    }
    this.data = value;
  }
  get values(): any {
    return this.data;
  }
  @Input() displayData: any;
  @Output() reloadDetails = new EventEmitter<void>();

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'wellDepth',
      title: 'Well Depth',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(4, 2)],
      width: 115,
    },
    {
      columnId: 'staticWaterLevel',
      title: 'Static Level',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(4, 2)],
      width: 115,
    },
    {
      columnId: 'castingDiameter',
      title: 'Casing Diameter',
      type: FormFieldTypeEnum.Input,
      validators: [WRISValidators.isNumber(2, 2)],
      width: 115,
    },
    {
      columnId: 'flowing',
      title: 'Flowing',
      type: FormFieldTypeEnum.Checkbox,
      displayInTable: false,
      width: 115,
    },
    {
      columnId: 'pumpSize',
      title: 'Pump Size',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [WRISValidators.isNumber(5, 2)],
      width: 115,
    },
    {
      columnId: 'waterTemp',
      title: 'Water Temp',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [WRISValidators.isNumber(3, 2)],
      width: 115,
    },
    {
      columnId: 'testRate',
      title: 'Test Rate',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [WRISValidators.isNumber(5, 2)],
      width: 115,
    },
  ];
  public title = 'Well Data';

  protected _get(): void {
    this.reloadDetails.emit();
  }

  public moreInfo(): void {
    this.dialog.open(MoreInfoDialogComponent, {
      width: '500px',
      data: {
        title: 'Well Data',
        columns: this.columns.filter(
          (c: ColumnDefinitionInterface) => !(c.displayInTable ?? true)
        ),
        values: {
          ...this.data,
        },
      },
    });
  }

  // Handle the onEdit event
  public onEdit(): void {
    WaterRightsPrivileges.checkVersionDecree(
      Number(this.idArray[1]),
      this.isDecreed,
      this.isEditableIfDecreed,
      this.dialog,
      this._displayEditDialog.bind(this, this.data)
    );
  }

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
      canPUT: this.endpointService.canPUT(this.service.url, 0) && this.canEdit,
    };
  }
}
