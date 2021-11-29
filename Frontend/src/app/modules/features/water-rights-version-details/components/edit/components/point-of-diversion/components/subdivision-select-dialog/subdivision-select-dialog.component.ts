import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { SubdivisionCodesService } from 'src/app/modules/shared/services/subdivision-codes.service';

@Component({
  selector: 'app-subdivision-select-dialog',
  templateUrl:
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
  ],
  providers: [SubdivisionCodesService],
})
export class SubdivisionSelectDialogComponent extends SearchSelectDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<SubdivisionSelectDialogComponent>,
    public service: SubdivisionCodesService
  ) {
    super(data, dialogRef, service);
  }

  public title = 'Select DNRC and DOR Names';
  public searchTitle = 'Search';
  public selectTitle = 'Pick a DNRC Name and DOR Name';
  public sortColumn = 'dnrcName';
}
