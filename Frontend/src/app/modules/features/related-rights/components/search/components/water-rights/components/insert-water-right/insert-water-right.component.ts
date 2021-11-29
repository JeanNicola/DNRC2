import { Component, Inject } from '@angular/core';
import { FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataManagementDialogInterface } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.interface';
import { InsertWaterRightTemplateComponent } from 'src/app/modules/shared/components/templates/insert-water-right-template/insert-water-right-template.component';
import { DataQueryParametersInterface } from 'src/app/modules/shared/interfaces/data-query-parameters.interface';
import { RelatedWaterRightVersionsService } from './services/related-water-right-versions.service';

@Component({
  selector: 'app-insert-water-right',
  templateUrl:
    '../../../../../../../../shared/components/templates/insert-water-right-template/insert-water-right-template.component.html',
  styleUrls: [
    '../../../../../../../../shared/components/templates/insert-water-right-template/insert-water-right-template.component.scss',
  ],
  providers: [RelatedWaterRightVersionsService],
})
export class InsertWaterRightComponent extends InsertWaterRightTemplateComponent {
  constructor(
    public service: RelatedWaterRightVersionsService,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface,
    public dialogRef: MatDialogRef<InsertWaterRightComponent>
  ) {
    super(service, data, dialogRef);
  }

  public idArray = [];
  public queryParameters: DataQueryParametersInterface = {
    sortDirection: 'asc',
    sortColumn: 'completeWaterRightNumber',
    pageSize: 25,
    pageNumber: 1,
    filters: {},
  };

  protected _onRowStateChangedHandler(idx) {
    let row = this.waterRights[idx];
    let formGroup = (this.checkboxesForm.get('rows') as FormArray).at(idx);
    if (formGroup.get('checked').value) {
      this.checkedWaterRights[`${row.waterRightId}-${row.versionId}`] =
        formGroup;
      this.selectedWaterRightsCount = this.selectedWaterRightsCount + 1;
    } else {
      delete this.checkedWaterRights[`${row.waterRightId}-${row.versionId}`];
      this.selectedWaterRightsCount = this.selectedWaterRightsCount - 1;
    }
  }

  protected initFunction() {
    if (this.data.values.relatedRightId) {
      this.idArray = [this.data.values.relatedRightId];
    } else {
      this.service.getAllWaterRights();
    }
  }

  protected postLookup(dataIn: any): any {
    // if only one row is returned, automatically accept the row.
    if (dataIn.results.length === 1 && dataIn.currentPage === 1) {
      this.dialogRef.close([dataIn.results[0]]);
    }
    return dataIn;
  }
}
