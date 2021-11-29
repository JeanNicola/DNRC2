import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-owners-application-dialog',
  templateUrl: './owners-application-dialog.component.html',
  styleUrls: ['./owners-application-dialog.component.scss'],
})
export class OwnersApplicationDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<OwnersApplicationDialogComponent>
  ) {}

  public onDblClick(applicationId: number): void {
    this.dialogRef.close(applicationId);
  }
}
