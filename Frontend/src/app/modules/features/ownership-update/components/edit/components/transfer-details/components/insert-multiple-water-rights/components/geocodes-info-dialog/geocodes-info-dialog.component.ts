import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-geocodes-info-dialog',
  templateUrl: './geocodes-info-dialog.component.html',
  styleUrls: ['./geocodes-info-dialog.component.scss'],
})
export class GeocodesInfoDialogComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<GeocodesInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef);
  }

  @ViewChild('tabs') tabs: MatTabGroup;

  public title = 'Review Information';
  public tooltip = 'Review Information';
  public displayedColumns: string[];
}
