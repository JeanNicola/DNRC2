import { Component } from '@angular/core';
import { InsertDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/insert-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-insert-update-period',
  templateUrl: './insert-update-period.component.html',
  styleUrls: [
    '../../../../../../shared/components/dialogs/data-management/data-management-dialog.component.scss',
  ],
})
export class InsertUpdatePeriodComponent extends InsertDialogComponent {
  public dialogModesEnum = DataManagementDialogModes;
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
