import { Component, Inject } from '@angular/core';
import { Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { WRISValidators } from 'src/app/modules/shared/validators/WRIS.validator';
import { MaxVolumeDescriptionDialogComponent } from '../max-volume-description-dialog/max-volume-description-dialog.component';

@Component({
  selector: 'app-max-volume-update-dialog',
  templateUrl: './max-volume-update-dialog.component.html',
  styleUrls: ['./max-volume-update-dialog.component.scss'],
})
export class MaxVolumeUpdateDialogComponent extends UpdateDialogComponent {
  constructor(
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<MaxVolumeUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef, data);
  }

  public onDescriptionInsert(values: any): void {
    const dialogRef = this.dialog.open(MaxVolumeDescriptionDialogComponent, {
      width: '700px',
      data: {
        title: 'Select a Max Volume Description',
        columns: [],
        values,
      },
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      this.formGroup.get('volumeDescription').setValue(result.description);
      this.formGroup.get('volumeDescription').markAsDirty();
      this.formGroup.get('volumeDescription').updateValueAndValidity();
    });
  }
}
