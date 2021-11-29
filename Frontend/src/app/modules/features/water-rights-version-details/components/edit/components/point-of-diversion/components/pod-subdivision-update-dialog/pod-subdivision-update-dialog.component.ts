import { AfterViewInit, Component, Inject } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { SubdivisionCodesService } from 'src/app/modules/shared/services/subdivision-codes.service';
import { SubdivisionSelectDialogComponent } from '../subdivision-select-dialog/subdivision-select-dialog.component';

@Component({
  selector: 'app-pod-subdivision-update-dialog',
  templateUrl: './pod-subdivision-update-dialog.component.html',
  styleUrls: ['./pod-subdivision-update-dialog.component.scss'],
  providers: [SubdivisionCodesService],
})
export class PodSubdivisionUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit
{
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA)
    public data: DataManagementDialogInterface & {
      secondColumns: ColumnDefinitionInterface[];
    },
    public dialog: MatDialog
  ) {
    super(dialogRef, data);
  }
  public displayFields = this._getDisplayFields(this.data.columns);
  public secondDisplayFields = this._getDisplayFields(this.data.secondColumns);

  public ngAfterViewInit(): void {
    setTimeout(() => {
      this.formGroup.get('dnrcName').disable();
      this.formGroup.get('dorName').disable();
    });
  }

  public searchColumns: ColumnDefinitionInterface[] = [
    {
      columnId: 'subdivisionCode',
      title: 'Subdivision Code',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
      displayInTable: false,
    },
    {
      columnId: 'dnrcName',
      title: 'DNRC Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'dorName',
      title: 'DOR Name',
      type: FormFieldTypeEnum.Input,
    },
    {
      columnId: 'countyName',
      title: 'County and State',
      type: FormFieldTypeEnum.Input,
      displayInSearch: false,
    },
  ];
  public subdivisionCode = this.data.values.subdivisionCode;

  public onSubdivisionSelect(): void {
    const dialogRef = this.dialog.open(SubdivisionSelectDialogComponent, {
      width: '800px',
      data: {
        title: 'Select a DNRC and DOR Name',
        columns: this.searchColumns,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.code) {
        this.formGroup.get('dnrcName').setValue(result.dnrcName);
        this.formGroup.get('dorName').setValue(result.dorName);
        this.subdivisionCode = result.code;
        this.formGroup.markAsDirty();
      }
    });
  }

  public save(): void {
    const subdivisionInfo = this.formGroup.getRawValue();
    delete subdivisionInfo.dnrcName;
    delete subdivisionInfo.dorName;
    subdivisionInfo.subdivisionCode = this.subdivisionCode;
    this.dialogRef.close(subdivisionInfo);
  }
}
