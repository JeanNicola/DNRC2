import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementDialogComponent } from '../data-management-dialog.component';
import { DataManagementDialogModes } from '../data-management-dialog.enum';
import { DataManagementDialogInterface } from '../data-management-dialog.interface';

@Component({
  selector: 'shared-insert-dialog',
  templateUrl: './../data-management-dialog.component.html',
  styleUrls: ['./../data-management-dialog.component.scss'],
})
export class InsertDialogComponent extends DataManagementDialogComponent {
  @Output() public blurEvent: EventEmitter<any> = new EventEmitter<any>();

  @Output()
  public changeEvent: EventEmitter<any> = new EventEmitter<any>();

  public formGroup: FormGroup = new FormGroup({}, this.data.validators);
  mode = DataManagementDialogModes.Insert;
  title = this.data.title;
  displayFields = this._getDisplayFields(this.data.columns);
  tooltip = 'Insert';

  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: DataManagementDialogInterface
  ) {
    super(dialogRef);
  }

  _onChange($event: MatSelectChange) {
    this.changeEvent.emit($event);
  }

  _onBlur($event: any): void {
    this.blurEvent.emit($event);
  }

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    return columns
      .filter((item) =>
        item?.displayInInsert == null ? true : item?.displayInInsert
      )
      .map((item) => ({
        ...item,
      }));
  }
}
