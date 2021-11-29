import {
  Component,
  ElementRef,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { Observable, Subscription } from 'rxjs';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';
import { DataManagementFormFieldComponent } from '../../data-form-field/data-form-field.component';
import { DataManagementDialogModes } from './data-management-dialog.enum';

@Component({
  template: '',
})
export abstract class DataManagementDialogComponent
  implements OnInit, OnDestroy
{
  public formGroup: FormGroup = new FormGroup({});
  public title: string;
  public displayFields: ColumnDefinitionInterface[];
  public mode: DataManagementDialogModes;
  public tooltip: string;

  // Reference used to keep keyboard tab focus on a single row during editing
  @ViewChildren(DataManagementFormFieldComponent)
  formFields: QueryList<DataManagementFormFieldComponent>;
  @ViewChild('cancel', { read: ElementRef }) cancelButton: ElementRef;

  @Input()
  public reloadColumns$: Observable<{
    columns: ColumnDefinitionInterface[];
    markAsDirty?;
    markAllAsTouched?;
  }>;

  public reloadColumnsSub: Subscription;

  constructor(public dialogRef: MatDialogRef<any>) {}

  public ngOnInit(): void {
    this.initFunction();
    this.setFocus();
    this.setReloadColumnsFunctionality();
  }

  public ngOnDestroy() {
    if (this.reloadColumnsSub) {
      this.reloadColumnsSub.unsubscribe();
    }
  }

  protected initFunction(): void {}

  protected afterFormWasReloaded() {}

  protected setReloadColumnsFunctionality() {
    if (this.reloadColumns$) {
      this.reloadColumnsSub = this.reloadColumns$.subscribe((data) => {
        // Keep track of the old form
        const oldFormControls = { ...this.formGroup.controls };
        // Reset fields
        this.displayFields = [];
        this.formGroup = new FormGroup({});
        this.displayFields = this._getDisplayFields(data.columns);
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

  protected setFocus(): void {
    this.dialogRef.afterOpened().subscribe(() => {
      const item = this.formFields.find((i) => i.isNotDisabled());
      if (item) {
        item.initFocus();
      }
    });
  }

  // When shift + tab is used on the first item of the formField array, focus on cancel button
  receiveShiftTab($event: KeyboardEvent, i: number): void {
    if (i === 0) {
      $event.preventDefault();
      this.cancelButton.nativeElement.focus();
    }
  }

  _onChange($event: MatSelectChange) {}

  _onBlur($event: any) {}

  protected _getDisplayFields(columns: ColumnDefinitionInterface[]) {
    return columns
      .filter((item) =>
        item?.displayInEdit == null ? true : item?.displayInEdit
      )
      .map((item) => ({
        ...item,
      }));
  }

  // If the enter key is pressed on the form, submit the form.
  public keyPress(event: KeyboardEvent): void {
    // if (event.key === 'Enter') {
    //   if (this.formGroup.valid) {
    //     this.save();
    //   }
    // }
  }

  public save(): void {
    this.dialogRef.close(this.formGroup.getRawValue());
  }

  public close(): void {
    this.dialogRef.close(null);
  }
}
