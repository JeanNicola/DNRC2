import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { InsertApplicantComponent } from 'src/app/modules/shared/components/insert-applicant/insert-applicant.component';
import { ApplicantSearchService } from 'src/app/modules/shared/components/insert-applicant/services/applicant-search.service';

@Component({
  selector: 'app-water-right-owner-search-dialog',
  templateUrl:
    '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
    '../../../../../../shared/components/insert-applicant/insert-applicant.component.scss',
  ],
  providers: [ApplicantSearchService],
})
export class WaterRightOwnerSearchDialogComponent extends InsertApplicantComponent {
  title = 'Add New Owner';
  public searchTitle = 'Search for Owner';
  public selectTitle = 'Select Owner';
  public addTooltip = 'Add Owner';
  public sortColumn = 'name';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<WaterRightOwnerSearchDialogComponent>,
    public service: ApplicantSearchService
  ) {
    super(data, dialogRef, service);
  }

  protected postLookup(dataIn: any): any {
    if (dataIn.totalElements === 1) {
      this.dialogRef.close(dataIn.results[0]);
    }
    return dataIn;
  }
}
