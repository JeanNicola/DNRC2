import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RepsApplicationDialogComponent } from 'src/app/modules/features/applications/components/search/reps-application-dialog/reps-application-dialog.component';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-ownership-update-dialog',
  templateUrl: './ownership-update-dialog.component.html',
  styleUrls: ['./ownership-update-dialog.component.scss'],
})
export class OwnershipUpdateDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<RepsApplicationDialogComponent>
  ) {
    super(dialogRef);
  }

  public onDblClick(ownershipUpdateId: any): void {
    if (typeof ownershipUpdateId === 'number') {
      this.dialogRef.close(ownershipUpdateId);
    }
  }
}
