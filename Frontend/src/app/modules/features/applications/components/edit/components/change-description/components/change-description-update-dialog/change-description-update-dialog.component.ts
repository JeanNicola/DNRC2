import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';

@Component({
  selector: 'change-description-update-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class ChangeDescriptionUpdateDialogComponent extends UpdateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ChangeDescriptionUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef, data);
  }

  initFunction(): void {
    this.setup(this.data);
  }

  // Generate fields based on Application Type Code Logic
  setup(data: any): void {
    const columnIdArr: string[] = [];
    if (data.distanceAndDirection) {
      columnIdArr.push('distance', 'direction');
    } else {
      columnIdArr.push('changeDescription');
    }
    if (data.pastUseOfWater) {
      columnIdArr.push('pastUse');
    }
    columnIdArr.push('additionalInformation');
    this.displayFields = this.data.columns.filter((column) =>
      columnIdArr.includes(column.columnId)
    );
  }
}
