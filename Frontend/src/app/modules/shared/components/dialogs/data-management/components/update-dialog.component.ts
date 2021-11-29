import {
  Component,
  EventEmitter,
  Inject,
  OnDestroy,
  Output,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementDialogComponent } from '../data-management-dialog.component';
import { DataManagementDialogModes } from '../data-management-dialog.enum';
import { DataManagementDialogInterface } from '../data-management-dialog.interface';

@Component({
  selector: 'shared-update-dialog',
  templateUrl: './../data-management-dialog.component.html',
  styleUrls: ['./../data-management-dialog.component.scss'],
})
export class UpdateDialogComponent extends DataManagementDialogComponent {
  @Output()
  public changeEvent: EventEmitter<any> = new EventEmitter<any>();

  @Output()
  public blurEvent: EventEmitter<Event> = new EventEmitter<Event>();

  mode = DataManagementDialogModes.Update;
  title = this.data.title;
  displayFields = this._getDisplayFields(this.data.columns);
  tooltip = 'Update';

  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef);
  }

  public _onChange($event: MatSelectChange) {
    this.changeEvent.emit($event);
  }

  public _onBlur($event: any) {
    this.blurEvent.emit($event);
  }

  protected setReloadColumnsFunctionality() {
    if (this.reloadColumns$) {
      this.reloadColumnsSub = this.reloadColumns$.subscribe((data) => {
        // Keep track of the old form
        const oldFormControls = { ...this.formGroup.controls };
        const oldData = { ...this.data };
        // Reset fields
        this.displayFields = [];
        this.formGroup = new FormGroup({});
        this.displayFields = this._getDisplayFields(data.columns);
        if (this.data) {
          this.data.values = {};
          this.displayFields.forEach((f) => {
            this.data.values[f.columnId] = oldData[f.columnId];
          });
        }
        // Restore values
        setTimeout(() => {
          data.columns.forEach((column: ColumnDefinitionInterface) => {
            if (
              oldFormControls[column.columnId] &&
              this.formGroup.get(column.columnId)
            ) {
              this.formGroup
                .get(column.columnId)
                .setValue(oldFormControls[column.columnId].value);
              // Mark has touched if it had a value
              if (oldFormControls[column.columnId].value) {
                this.formGroup.get(column.columnId).markAsTouched();
              }
            }
          });
          if (data.markAsDirty) {
            this.formGroup.markAsDirty();
          }
          if (data.markAllAsTouched) {
            this.formGroup.markAllAsTouched();
          }

          this.afterFormWasReloaded();
        });
      });
    }
  }

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    return columns
      .filter((item) =>
        item?.displayInEdit == null ? true : item?.displayInEdit
      )
      .map((item) => ({
        ...item,
      }));
  }
}
