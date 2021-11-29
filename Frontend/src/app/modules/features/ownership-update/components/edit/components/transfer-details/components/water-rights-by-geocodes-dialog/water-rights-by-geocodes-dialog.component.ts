import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';

@Component({
  selector: 'app-water-rights-by-geocodes-dialog',
  templateUrl: './water-rights-by-geocodes-dialog.component.html',
  styleUrls: ['./water-rights-by-geocodes-dialog.component.scss'],
})
export class WaterRightsByGeocodesDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<WaterRightsByGeocodesDialogComponent>,
    public snackBar: SnackBarService,
    public route: ActivatedRoute
  ) {
    super(dialogRef);
  }

  public ownershipUpdateId = this.data.values.ownershipUpdateId;
  public checkedWaterRights = {};
  public selectedWaterRightsCount = 0;
  public title = this.data.title;

  public onRowStateChangedHandler({ row, formGroup }) {
    if (formGroup.get('checked').value) {
      this.checkedWaterRights[row.waterRightId] = formGroup;
      this.selectedWaterRightsCount = this.selectedWaterRightsCount + 1;
    } else {
      delete this.checkedWaterRights[row.waterRightId];
      this.selectedWaterRightsCount = this.selectedWaterRightsCount - 1;
    }
  }

  public onRowDoubleClick(record: any): void {
    this.dialogRef.close([record]);
  }

  public save(): void {
    this.dialogRef.close(
      Object.values(this.checkedWaterRights).map((formGroup: FormGroup) => {
        return formGroup.getRawValue();
      })
    );
  }
}
