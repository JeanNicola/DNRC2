import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-water-rights-dialog',
  templateUrl: './water-rights-dialog.component.html',
  styleUrls: ['./water-rights-dialog.component.scss'],
})
export class WaterRightsDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<WaterRightsDialogComponent>
  ) {
    super(dialogRef);
  }
}
