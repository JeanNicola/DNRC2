import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { WaterRightsService } from 'src/app/modules/shared/components/affected-water-rights/components/insert-water-right/services/water-rights.service';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { SearchSelectDialogComponent } from 'src/app/modules/shared/components/dialogs/search-select-dialog/search-select-dialog.component';

@Component({
  selector: 'app-water-right-select-dialog',
  templateUrl: '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.html',
  styleUrls: [
    './water-right-select-dialog.component.scss',
    '../../../../../../shared/components/dialogs/search-select-dialog/search-select-dialog.component.scss'
  ],
  providers: [WaterRightsService],
})
export class WaterRightSelectDialogComponent extends SearchSelectDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<WaterRightSelectDialogComponent>,
    public service: WaterRightsService
  ) {
    super(data, dialogRef, service);
  }

  public mode = this.data.mode;
  public title = this.data.title;
  public searchTitle = 'Search Water Rights';
  public selectTitle = 'Select a Water Right';
  public sortColumn = 'completeWaterRightNumber';

  protected postLookup(dataIn: any): any {
    if(dataIn.totalElements === 1) {
      this.dialogRef.close(dataIn.results[0]);
    }
    return dataIn;
  }

  public stepping(step: StepperSelectionEvent): void {
    // We're going backwards so reset the already "completed" steps
    if (step.previouslySelectedIndex > step.selectedIndex) {
      if (step.previouslySelectedIndex === 0) {
        this.rows = [];
      }

      this.stepper.steps.forEach((v, i) => {
        if (i > step.selectedIndex) {
          v.completed = false;
        }
      });
    } else {
      if (step.selectedIndex === 1) {
        this.queryParameters.filters = { statusCode: 'ACTV', ...this.formGroup.value };
        this.queryParameters.pageNumber = 1;
        this.lookup();
      }
      this.stepper.selected.completed = true;
    }
  }
}
