import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogComponent } from '../data-management-dialog.component';
import { DataManagementDialogModes } from '../data-management-dialog.enum';
import { DataManagementDialogInterface } from '../data-management-dialog.interface';

@Component({
  selector: 'shared-search-dialog',
  templateUrl: './../data-management-dialog.component.html',
  styleUrls: ['./../data-management-dialog.component.scss'],
})
export class SearchDialogComponent extends DataManagementDialogComponent {
  mode = DataManagementDialogModes.Search;
  public formGroup: FormGroup = new FormGroup({}, this.data.validators);
  displayFields = this.data.columns.filter((item) =>
    item?.displayInSearch == null ? true : item?.displayInSearch
  );
  tooltip = 'Search';

  constructor(
    public dialogRef: MatDialogRef<SearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef);
    this.title = this.data?.title;
  }

  public keyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      if (this.formGroup.valid) {
        this.save();
      }
    }
  }
}
