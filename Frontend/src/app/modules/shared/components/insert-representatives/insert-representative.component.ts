import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { InsertApplicantComponent } from '../insert-applicant/insert-applicant.component';
import { ApplicantSearchService } from '../insert-applicant/services/applicant-search.service';

@Component({
  selector: 'app-insert-representative-dialog',
  templateUrl: './insert-representative.component.html',
  styleUrls: ['../insert-applicant/insert-applicant.component.scss'],
  providers: [ApplicantSearchService],
})
export class InsertRepresentativeComponent extends InsertApplicantComponent {
  public title = 'Add New Representative';
  public inputDataFormGroup: FormGroup = new FormGroup({});

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: InsertRepresentativeInterface,
    public dialogRef: MatDialogRef<InsertApplicantComponent>,
    public service: ApplicantSearchService
  ) {
    super(data, dialogRef, service);
  }

  // Allows any children to have local processing once data is returned.
  protected postLookup(dataIn: any): void {
    return dataIn;
  }

  // If the user walks backwards from the last page, clear out the choices
  public stepping(step: StepperSelectionEvent): void {
    super.stepping(step);
    // Siced they returned to the selection screen, unset the current selection
    if (step.selectedIndex < 2) {
      this.row = null;
    }
  }

  // select row and move to next step
  public onRowDoubleClick(idx: number): void {
    this.row = this.rows[idx];
    this.stepper.next();
  }

  public save(): void {
    this.dialogRef.close({
      ...this.row,
      ...this.inputDataFormGroup.getRawValue(),
    });
  }
}

export interface InsertRepresentativeInterface
  extends DataManagementDialogInterface {
  formColumns: ColumnDefinitionInterface[];
}
