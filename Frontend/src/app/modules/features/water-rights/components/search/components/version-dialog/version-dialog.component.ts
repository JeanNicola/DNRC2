import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-version-dialog',
  templateUrl: './version-dialog.component.html',
  styleUrls: ['./version-dialog.component.scss'],
})
export class VersionDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<VersionDialogComponent>
  ) {}
}
