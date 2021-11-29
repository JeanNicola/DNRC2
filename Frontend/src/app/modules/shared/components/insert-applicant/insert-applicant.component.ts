import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from '../dialogs/search-select-dialog/search-select-dialog.component';
import { ApplicantSearchService } from './services/applicant-search.service';

@Component({
  selector: 'app-insert-applicant',
  templateUrl:
    '../dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    '../dialogs/search-select-dialog/search-select-dialog.component.scss',
    './insert-applicant.component.scss',
  ],
  providers: [ApplicantSearchService],
})
export class InsertApplicantComponent extends SearchSelectDialogComponent {
  title = 'Add New Applicant';
  public searchTitle = 'Search for Applicant';
  public selectTitle = 'Select Applicant';
  public addTooltip = 'Add Applicant';
  public sortColumn = 'name';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertApplicantComponent>,
    public service: ApplicantSearchService
  ) {
    super(data, dialogRef, service);
  }
}
