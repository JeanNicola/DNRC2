import { Component, Inject } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { SearchService as SearchApplicationsService } from 'src/app/modules/shared/services/search.service';
import { CaseProgramTypes } from '../../../../../create/enums/caseProgramTypes';
import { DecreeBasinsService } from '../../../../../create/services/decree-basins.service';

@Component({
  selector: 'app-update-cases-dialog',
  templateUrl: './update-cases-dialog.component.html',
  styleUrls: [],
  providers: [DecreeBasinsService, SearchApplicationsService],
})
export class UpdateCasesDialogComponent extends UpdateDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<any>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public decreeBasinsService: DecreeBasinsService,
    public searchApplicationsService: SearchApplicationsService
  ) {
    super(dialogRef, data);
  }

  // Dialog state
  public programTypes = CaseProgramTypes;
  public currentProgramType = this.data.values?.programType;
  public formIsDirty = false;

  public onTypeChangeHandler($event) {
    this.formIsDirty = true;
    const program = this.data.programsDictionary[$event.value];
    if (program !== this.currentProgramType) {
      this.data.values = {
        ...this.data.values,
        caseStatus: $event.data?.caseStatus,
        caseType: $event.data?.caseType,
        officeId: $event.data?.officeId,
      };
    }
    this.currentProgramType = program;
  }

  public onSaveHandler(data) {
    this.dialogRef.close(data);
  }
}
