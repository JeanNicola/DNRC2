import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { CompactSearchService } from 'src/app/modules/shared/services/compact-search.service';

@Component({
  selector: 'app-change-compact-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
  ],
  providers: [CompactSearchService],
})
export class ChangeCompactDialogComponent extends SearchSelectDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<ChangeCompactDialogComponent>,
    public service: CompactSearchService
  ) {
    super(data, dialogRef, service);
  }

  mode = DataManagementDialogModes.Update;
  title = 'Change Subcompact and Compact';
  public searchTitle = 'Search for Subcompacts';
  public selectTitle = 'Select a Subcompact';
  public addTooltip = 'Change the Subcompact';
  public sortColumn = 'compact';
}
