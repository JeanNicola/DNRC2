import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-child-rights-dialog',
  templateUrl: './child-rights-dialog.component.html',
  styleUrls: ['./child-rights-dialog.component.scss'],
})
export class ChildRightsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ChildRightsDialogComponent>
  ) {}

  public onDblClick(waterRightId: number): void {
    this.dialogRef.close(waterRightId);
  }
}
