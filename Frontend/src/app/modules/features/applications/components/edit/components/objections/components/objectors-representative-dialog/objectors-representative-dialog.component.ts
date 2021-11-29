import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-objectors-representative-dialog',
  templateUrl: './objectors-representative-dialog.component.html',
  styleUrls: ['./objectors-representative-dialog.component.scss'],
})
export class ObjectorsRepresentativeDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ObjectorsRepresentativeDialogComponent>
  ) {}
}
