import { Component, Inject } from '@angular/core';
import { FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { InsertWaterRightTemplateComponent } from '../../../templates/insert-water-right-template/insert-water-right-template.component';
import { WaterRightsService } from './services/water-rights.service';

@Component({
  selector: 'app-insert-water-right',
  templateUrl:
    '../../../templates/insert-water-right-template/insert-water-right-template.component.html',
  styleUrls: [
    '../../../templates/insert-water-right-template/insert-water-right-template.component.scss',
  ],
  providers: [WaterRightsService],
})
export class InsertWaterRightComponent extends InsertWaterRightTemplateComponent {
  constructor(
    public service: WaterRightsService,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertWaterRightComponent>
  ) {
    super(service, data, dialogRef);
  }

  protected _onRowStateChangedHandler(idx) {
    let row = this.waterRights[idx];
    let formGroup = (this.checkboxesForm.get('rows') as FormArray).at(idx);
    if (formGroup.get('checked').value) {
      this.checkedWaterRights[row.waterRightId] = formGroup;
      this.selectedWaterRightsCount = this.selectedWaterRightsCount + 1;
    } else {
      delete this.checkedWaterRights[row.waterRightId];
      this.selectedWaterRightsCount = this.selectedWaterRightsCount - 1;
    }
  }
}
