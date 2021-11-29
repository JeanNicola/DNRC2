import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogComponent } from '../../data-management-dialog.component';
import { DataManagementDialogModes } from '../../data-management-dialog.enum';
import { DataManagementDialogInterface } from '../../data-management-dialog.interface';
// Imports are used in template
import {
  dateFormatString,
  dateTimeFormatString,
  monthDayDateFormatString,
} from 'src/app/modules/shared/constants/date-format-strings';

@Component({
  selector: 'shared-more-info-dialog',
  templateUrl: './more-info-dialog.component.html',
  styleUrls: ['./more-info-dialog.component.scss'],
})
export class MoreInfoDialogComponent extends DataManagementDialogComponent {
  mode = DataManagementDialogModes.Search;
  title = this.data.title + ': More Info';
  tooltip = 'More Info';
  displayedColumns: string[];
  verticalLayout = false;

  // Strings used for formatting with template date pipe
  public dateFormatString = dateFormatString;
  public dateTimeFormatString = dateTimeFormatString;
  public monthDayDateTimeFormatString = monthDayDateFormatString;

  constructor(
    public dialogRef: MatDialogRef<MoreInfoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MoreInfoDialogInterface
  ) {
    super(dialogRef);
  }

  initFunction(): void {
    this.displayedColumns = this.data.columns.map((item) => item.columnId);

    if (this.data.verticalLayout === true) {
      this.verticalLayout = true;
    }
  }
}

interface MoreInfoDialogInterface extends DataManagementDialogInterface {
  verticalLayout: boolean;
}
