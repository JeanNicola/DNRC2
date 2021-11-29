import { Component, Input } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import {
  dateDisplayFormat,
  dateTimeDtoFormat,
} from 'src/app/modules/shared/constants/date-time-formats';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { MeasurementVariableService } from '../../services/measurement-variable.service';

@Component({
  selector: 'app-measurement-variables-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './measurement-variables-table.component.scss',
  ],
  providers: [MeasurementVariableService],
})
export class MeasurementVariablesTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: MeasurementVariableService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
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

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'comment',
      title: 'Comment',
      type: FormFieldTypeEnum.TextArea,
      width: 500,
    },
  ];

  public title = 'Comments';
  public searchable = false;
  public hideDelete = true;
  public hideInsert = true;
  public dialogWidth = '600px';
  public paging = false;

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

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.commentId];
  }

  protected _buildEditDto(originalData: any, editedData: any): any {
    if (originalData?.variableType === 'DATE' && editedData?.variableText) {
      editedData.variableText = moment(
        editedData.variableText,
        dateTimeDtoFormat
      ).format(dateDisplayFormat);
    }
    return editedData;
  }

  protected _displayEditDialog(dataIn: any): void {
    // copying so changes in data and type don't affect the table
    const data = { ...dataIn };
    const { variableType, variableText, maxLength = 2600 } = dataIn;
    const columns = [
      ...this.columns.map((col: ColumnDefinitionInterface) => ({ ...col })),
    ];
    const varColumn = columns.find(
      (col: ColumnDefinitionInterface) => col.columnId === 'comment'
    );
    if (variableType === 'NUMERIC') {
      varColumn.type = FormFieldTypeEnum.TextArea;
      varColumn.validators = [WRISValidators.isNumber()];
    } else if (variableType === 'DATE') {
      if (variableText) {
        data.variableText = moment(variableText, dateDisplayFormat).format(
          dateTimeDtoFormat
        );
      }
      varColumn.type = FormFieldTypeEnum.Date;
      varColumn.validators = [];
    } else {
      varColumn.type = FormFieldTypeEnum.TextArea;
      varColumn.validators = [Validators.maxLength(maxLength)];
    }
    // Open the dialog
    const dialogRef = this.dialog.open(UpdateDialogComponent, {
      width: this.dialogWidth,
      data: {
        title: `Update ${this.title} Record`,
        columns,
        values: data,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result !== null) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected setInitialFocus(): void {}
  protected setTableFocus(): void {}
}
