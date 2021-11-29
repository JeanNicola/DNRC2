import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';

@Component({
  selector: 'app-review-information',
  templateUrl: './review-information.component.html',
  styleUrls: ['./review-information.component.scss'],
})
export class ReviewInformationComponent extends DataManagementDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ReviewInformationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef);
  }

  title = 'Water Rights';
  tooltip = 'Water Rights';
  displayedColumns: string[];
}
