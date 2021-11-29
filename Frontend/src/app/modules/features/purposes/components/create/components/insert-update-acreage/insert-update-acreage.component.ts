import { Component, Inject } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { EndpointsService } from 'src/app/modules/core/services/endpoint/endpoints.service';
import { SnackBarService } from 'src/app/modules/core/services/snack-bar/snack-bar.service';
import { DataManagementDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-insert-update-acreage.component',
  templateUrl: './insert-update-acreage.component.html',
  styleUrls: [
    './insert-update-acreage.component.scss',
    '../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class InsertUpdateAcreageComponent extends DataManagementDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<InsertUpdateAcreageComponent>,
    public endpointService: EndpointsService,
    public dialog: MatDialog,
    public snackBar: SnackBarService
  ) {
    super(dialogRef);
  }
  public dialogModesEnum = DataManagementDialogModes;

  public title = this.data.title;
  public placeOfUseDisplayFields = this._getDisplayFields(
    this.data.placeOfUseColumns
  );
  public firstLegalLandDescriptionDisplayFields = this._getDisplayFields(
    this.data.firstLegalLandDescriptionColumns
  );
  public secondLegalLandDescriptionDisplayFields = this._getDisplayFields(
    this.data.secondLegalLandDescriptionColumns
  );
  public dataManagementDialogModes = DataManagementDialogModes;

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    const filterFunction =
      this.mode === DataManagementDialogModes.Update
        ? (field) => field?.displayInEdit ?? true
        : (field) => field?.displayInInsert ?? true;
    return columns.filter(filterFunction).map((item) => ({
      ...item,
    }));
  }
}
