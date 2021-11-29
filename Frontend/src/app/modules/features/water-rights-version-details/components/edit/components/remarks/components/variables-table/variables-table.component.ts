import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import * as moment from 'moment';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { ErrorMessageEnum } from 'src/app/modules/features/code-tables/enums/error-message.enum';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { BaseCodeTableComponent } from 'src/app/modules/shared/components/templates/code-table/code-table.template';
import { dateTimeDtoFormat } from 'src/app/modules/shared/constants/date-format-strings';
import { dateDisplayFormat } from 'src/app/modules/shared/constants/date-time-formats';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { ErrorBodyInterface } from 'src/app/modules/shared/interfaces/error-body.interface';
import { WaterRightsPrivileges } from 'src/app/modules/shared/utilities/WaterRightsPrivilegesCheck';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { VariablesForRemarkService } from './services/variables-for-remark.service';

@Component({
  selector: 'app-variables-table',
  templateUrl:
    '../../../../../../../../shared/components/templates/code-table/code-table.template.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/code-table/code-table.template.scss',
    './variables-table.component.scss',
  ],
  providers: [VariablesForRemarkService],
})
export class VariablesTableComponent extends BaseCodeTableComponent {
  constructor(
    public service: VariablesForRemarkService,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(service, endpointService, dialog, snackBar);
  }

  @Input() canEdit = false;
  @Input() isDecreed = false;
  @Input() isEditableIfDecreed = false;

  @Input() set idArray(id: string[]) {
    if (id) {
      if (id.includes(null)) {
        this.dataMessage = 'No data found';
      } else if (!id.includes(undefined)) {
        this._idArray = id;
        this._get();
      }
    }
  }

  get idArray(): string[] {
    return super.idArray;
  }

  @Output() variableUpdate: EventEmitter<void> = new EventEmitter<void>();

  public primarySortColumn = 'variableNumberType';
  public title = 'Variables';
  public hideInsert = true;
  public hideDelete = true;
  public searchable = false;
  public isInMain = false;
  public dialogWidth = '500px';

  public columns: ColumnDefinitionInterface[] = [
    {
      columnId: 'variableNumberType',
      title: '# - Type',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'precedingText',
      title: 'Preceding Text',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
    {
      columnId: 'variableText',
      title: 'Variable',
      type: FormFieldTypeEnum.TextArea,
      validators: [],
    },
    {
      columnId: 'trailingText',
      title: 'Trailing Text',
      type: FormFieldTypeEnum.Input,
      displayInEdit: false,
    },
  ];

  protected setPermissions(): void {
    super.setPermissions();
    // Override the default canPUT value
    this.permissions = {
      ...this.permissions,
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
      (col: ColumnDefinitionInterface) => col.columnId === 'variableText'
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
      if (result !== null && result !== undefined) {
        this._update(this._buildEditDto(data, result), data);
      }
    });
  }

  protected _buildEditIdArray(dto: any, originalData?: any): string[] {
    return [...this.idArray, originalData.dataId];
  }

  protected _update(updatedRow: any, originalData?: any): void {
    this._getUpdateService()
      .update(updatedRow, ...this._buildEditIdArray(updatedRow, originalData))
      .subscribe(
        (dto) => {
          let messages = ['Record successfully updated.'];
          if (!!dto?.messages) {
            messages = [...dto.messages, ...messages];
          }
          this.snackBar.open(messages.join('\n'));
          this.variableUpdate.emit();
          this._get();
        },
        (err: HttpErrorResponse) => {
          const errorBody = err.error as ErrorBodyInterface;
          let message = 'Cannot update record. ';
          message += errorBody.userMessage || ErrorMessageEnum.PUT;
          this.snackBar.open(message);

          // Redisplay the dialog with the input data
          this._displayEditDialog({ ...originalData, ...updatedRow });
        }
      );
  }

  // Override the initial focus
  protected setInitialFocus(): void {}

  protected setTableFocus(): void {}
}
