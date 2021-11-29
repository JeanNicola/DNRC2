import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { ApplicantSearchService } from 'src/app/modules/shared/components/insert-applicant/services/applicant-search.service';

@Component({
  selector: 'app-interested-party-select-dialog',
  templateUrl: '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    './interested-party-select-dialog.component.scss',
    '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss'
  ],
  providers: [ApplicantSearchService],
})
export class InterestedPartySelectDialogComponent extends SearchSelectDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InterestedPartySelectDialogComponent>,
    public service: ApplicantSearchService,
  ) {
    super(data, dialogRef, service);
  }

  public mode = DataManagementDialogModes.Insert;
  public title = this.data.title;
  public searchTitle = 'Search Contacts';
  public selectTitle = 'Select a Contact';
  public sortColumn = 'firstLastName';

  protected postLookup(dataIn: any): any {
    if(dataIn.totalElements === 1) {
      this.dialogRef.close(dataIn.results[0]);
    }
    return dataIn;
  }
}
