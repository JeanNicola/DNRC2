import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';
import { SearchService } from 'src/app/modules/shared/services/search.service';

@Component({
  selector: 'app-application-select-dialog',
  templateUrl: './application-select-dialog.component.html',
  styleUrls: [
    './application-select-dialog.component.scss',
    '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss',
  ],
  providers: [SearchService],
})
export class ApplicationSelectDialogComponent extends SearchSelectDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<ApplicationSelectDialogComponent>,
    public service: SearchService
  ) {
    super(data, dialogRef, service);
  }

  public mode = this.data.mode;
  public title = this.data.title;
  public searchTitle = 'Search Applications';
  public selectTitle = 'Pick an Application';

  protected postLookup(dataIn: any): any {
    dataIn.results = dataIn.results.map(row => ({
      ...row,
      applicationTypeDescription: `${row.applicationTypeCode} - ${row.applicationTypeDescription}`,
    }));
    if(dataIn.totalElements === 1) {
      this.dialogRef.close(dataIn.results[0]);
    }
    return dataIn;
  }
}
