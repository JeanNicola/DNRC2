import { Component, Inject } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface, SelectionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { FlowRateDescriptionDialogComponent } from '../flow-rate-description-dialog/flow-rate-description-dialog.component';

@Component({
  selector: 'app-flow-rate-summary-update-dialog',
  templateUrl: './flow-rate-summary-update-dialog.component.html',
  styleUrls: ['./flow-rate-summary-update-dialog.component.scss']
})
export class FlowRateSummaryUpdateDialogComponent extends UpdateDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<FlowRateSummaryUpdateDialogComponent>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface & {
      originSelectArray: SelectionInterface[];
      unitSelectArray: SelectionInterface[];
    }
  ) {
    super(dialogRef, data);
  }

  public firstColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'flowRate',
      title: 'Flow Rate',
      type: FormFieldTypeEnum.Input,
      displayInTable: false,
      validators: [WRISValidators.isNumber(8, 2), WRISValidators.notAllowedIfAnyOtherFieldsNonNull({columnId: 'flowRateDescription', title: 'Flow Rate Description'})],
    },
    {
      columnId: 'flowRateUnit',
      title: 'Flow Rate Unit',
      type: FormFieldTypeEnum.Select,
      validators: [WRISValidators.notAllowedIfAnyOtherFieldsNonNull({columnId: 'flowRateDescription', title: 'Flow Rate Description'})],
    },
    {
      columnId: 'originCode',
      title: 'Origin',
      type: FormFieldTypeEnum.Select,
      displayInTable: false,
      validators: [Validators.required],
    },
  ];
  public secondColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'flowRateDescription',
      title: 'Flow Rate Description',
      type: FormFieldTypeEnum.TextArea,
      formWidth: 280,
      validators: [Validators.maxLength(350), WRISValidators.notAllowedIfAnyOtherFieldsNonNull({columnId: 'flowRateUnit', title: 'Flow Rate Unit'}, {columnId: 'flowRate', title: 'Flow Rate'})],
    },
  ];

  protected initFunction(): void {
    this._getColumn('originCode').selectArr = this.data.originSelectArray;
    this._getColumn('flowRateUnit').selectArr = this.data.unitSelectArray;
  }

  public onDescriptionInsert(values: any): void {
    const dialogRef = this.dialog.open(FlowRateDescriptionDialogComponent, {
      width: '700px',
      data: {
        title: 'Select a Flow Rate Description',
        columns: [],
        values,
      },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      this.formGroup.get('flowRateDescription').setValue(result.description);
      this.formGroup.get('flowRateDescription').markAsDirty();
      this.formGroup.get('flowRateDescription').updateValueAndValidity();
    });
  }

  private _getColumn(columnId: string) {
    return [
      ...this.firstColumns,
      ...this.secondColumns,
    ].find((c: ColumnDefinitionInterface) => c.columnId === columnId);
  }
}
