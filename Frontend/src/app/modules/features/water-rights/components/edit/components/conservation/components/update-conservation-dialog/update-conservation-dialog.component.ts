import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-update-conservation-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class UpdateConservationDialogComponent extends UpdateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef, data);
  }

  public _onChange($event): void {
    if (
      $event.fieldName == 'conservationDistrictNumber' &&
      $event.value == null
    ) {
      this.formGroup.get('conservationDistrictDate').patchValue(null);
      this.formGroup.get('conservationDistrictDate').markAsDirty();
      this.formGroup.get('conservationDistrictDate').markAsTouched();
      this.formGroup.get('conservationDistrictDate').updateValueAndValidity();
    }
  }
}
