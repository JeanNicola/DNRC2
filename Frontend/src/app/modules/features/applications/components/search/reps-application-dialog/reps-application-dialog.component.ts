import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-reps-application-dialog',
  templateUrl: './reps-application-dialog.component.html',
  styleUrls: ['./reps-application-dialog.component.scss'],
})
export class RepsApplicationDialogComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<RepsApplicationDialogComponent>
  ) {
    super(dialogRef);
  }

  public onDblClick(applicationId: any): void {
    if (typeof applicationId === 'number') {
      this.dialogRef.close(applicationId);
    }
  }
}
