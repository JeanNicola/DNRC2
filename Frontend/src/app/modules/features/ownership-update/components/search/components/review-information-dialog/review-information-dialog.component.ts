import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-review-information-dialog',
  templateUrl: './review-information-dialog.component.html',
  styleUrls: ['./review-information-dialog.component.scss'],
})
export class ReviewInformationDialogComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ReviewInformationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef);
  }

  @ViewChild('tabs') tabs: MatTabGroup;

  title = 'Review Information';
  tooltip = 'Review Information';
  displayedColumns: string[];
}
