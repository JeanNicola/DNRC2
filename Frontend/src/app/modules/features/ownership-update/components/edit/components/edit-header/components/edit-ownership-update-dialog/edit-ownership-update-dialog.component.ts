import { AfterViewInit, Component, Inject, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { UpdateDialogComponent } from 'src/app/modules/shared/components/dialogs/data-management/components/update-dialog.component';
import { DataManagementDialogModes } from 'src/app/modules/shared/components/dialogs/data-management/data-management-dialog.enum';
import { FormFieldTypeEnum } from 'src/app/modules/shared/enums/form-field-type.enum';
import { ColumnDefinitionInterface } from 'src/app/modules/shared/interfaces/column-definition.interface';

@Component({
  selector: 'app-edit-ownership-update-dialog',
  templateUrl: './edit-ownership-update-dialog.component.html',
  styleUrls: ['./edit-ownership-update-dialog.component.scss'],
})
export class EditOwnershipUpdateDialogComponent
  extends UpdateDialogComponent
  implements AfterViewInit, OnDestroy
{
  constructor(
    public dialogRef: MatDialogRef<EditOwnershipUpdateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    super(dialogRef, data);
  }

  public reloadColumnsSubject = new Subject<{
    columns: ColumnDefinitionInterface[];
    markAsDirty?;
    markAllAsTouched?;
  }>();

  public mode = DataManagementDialogModes.Update;
  public title = 'Update Ownership Update Record';
  public tooltip = 'Update';
  public displayFields = this._getDisplayFields(this.data.columns);

  public checkboxesState: any = {
    isPendingDor: this.data.values.isPendingDor,
    isReceivedAs608: this.data.values.isReceivedAs608,
  };
  // Info for the checkboxes below the title
  public ownershipUpdateCheckboxes: ColumnDefinitionInterface[] = [
    {
      columnId: 'isPendingDor',
      title: 'Pending DOR Validation',
      type: FormFieldTypeEnum.Checkbox,
    },
    {
      columnId: 'isReceivedAs608',
      title: 'Received as a 608',
      type: FormFieldTypeEnum.Checkbox,
    },
  ];

  initFunction() {
    this.reloadColumns$ = this.reloadColumnsSubject.asObservable();
  }

  public ngOnDestroy(): void {
    if (this.reloadColumnsSub) {
      this.reloadColumnsSub.unsubscribe();
    }
  }

  ngAfterViewInit() {
    // SetTimeout is used to avoid error ExpressionChangedAfterItHasBeenCheckedError
    setTimeout(() => {
      this.ownershipUpdateTypeHandler(
        this.data.values?.ownershipUpdateType,
        false
      );
      if (this.data.values?.dateProcessed || this.data.values?.dateTerminated) {
        this.handleProcessedOrTerminatedDate(
          this.data.values.dateProcessed || this.data.values.dateTerminated
        );
        this.formGroup.get('dateTerminated')?.disable();
        if (
          ['DOR 608', '608'].includes(this.data.values?.ownershipUpdateType)
        ) {
          this.formGroup.get('dateTerminated')?.enable();
        }
      }
    });
  }

  public _getColumn(
    columnId: string,
    columns: ColumnDefinitionInterface[]
  ): ColumnDefinitionInterface {
    const cols: ColumnDefinitionInterface[] = columns.filter(
      (c: ColumnDefinitionInterface) => c.columnId === columnId
    );

    return cols.length > 0 ? cols[0] : null;
  }

  _onChange(event) {
    if (event?.fieldName === 'dateTerminated') {
      this.handleProcessedOrTerminatedDate(event.target.value);
    }

    if (event?.fieldName === 'ownershipUpdateType') {
      this.setColumnsForOwnershipUpdateType(event.value);
    }
  }

  private setColumnsForOwnershipUpdateType(ownershipUpdateType: string) {
    const columns: any = [
      ...this.data.columns.map((column) => ({ ...column })),
    ];
    if (ownershipUpdateType === 'DOR 608') {
      this._getColumn('dateReceived', columns).title = 'Sale Date';
    } else {
      this._getColumn('dateReceived', columns).title = 'Received Date';
    }
    // Synchronize checkboxes state
    Object.keys(this.checkboxesState).forEach((key) => {
      this.checkboxesState[key] = this.formGroup.get(key).value;
    });
    // Send event in order for the new columns to be displayed
    this.reloadColumnsSubject.next({
      columns,
      markAllAsTouched: true,
      markAsDirty: true,
    });

    setTimeout(() => {
      this.ownershipUpdateTypeHandler(ownershipUpdateType);
    });
  }

  protected setReloadColumnsFunctionality() {
    if (this.reloadColumns$) {
      this.reloadColumnsSub = this.reloadColumns$.subscribe((data) => {
        // Keep track of the old form
        const oldFormControls = { ...this.formGroup.controls };
        // Reset fields
        this.displayFields = [];
        this.formGroup = new FormGroup({});
        // Re-render form
        this.displayFields = this._getDisplayFields(data.columns);
        this.ownershipUpdateCheckboxes = this.ownershipUpdateCheckboxes.map(
          (checkbox) => ({ ...checkbox })
        );
        // Restore values from old form
        setTimeout(() => {
          data.columns.forEach((column: ColumnDefinitionInterface) => {
            if (
              oldFormControls[column.columnId] &&
              this.formGroup.get(column.columnId)
            ) {
              this.formGroup
                .get(column.columnId)
                .setValue(oldFormControls[column.columnId].value);
            }
          });
          if (data.markAsDirty) {
            this.formGroup.markAsDirty();
          }
          if (data.markAllAsTouched) {
            this.formGroup.markAllAsTouched();
          }
        });
      });
    }
  }

  private ownershipUpdateTypeHandler(
    ownershipUpdateType: string,
    allowedToChangeValues = true
  ) {
    if (ownershipUpdateType === 'DOR 608') {
      this.formGroup.get('isPendingDor')?.enable();
      this.formGroup.get('isReceivedAs608')?.enable();
      if (
        !this.formGroup.get('isReceivedAs608').value &&
        allowedToChangeValues
      ) {
        this.formGroup.get('isPendingDor').setValue(true);
      }
    } else {
      this.formGroup.get('isPendingDor')?.disable();
      this.formGroup.get('isReceivedAs608')?.disable();
      if (allowedToChangeValues) {
        this.formGroup.get('isPendingDor').setValue(false);
        this.formGroup.get('isReceivedAs608').setValue(false);
      }
    }
  }

  private handleProcessedOrTerminatedDate(date) {
    if (!date && !this.data.values?.dateProcessed) {
      this.formGroup.get('ownershipUpdateType')?.enable();
      this.formGroup.get('dateReceived')?.enable();
      if (
        ['DOR 608'].includes(this.formGroup.get('ownershipUpdateType').value)
      ) {
        this.formGroup.get('isPendingDor')?.enable();
        this.formGroup.get('isReceivedAs608')?.enable();
      }
    } else {
      this.formGroup.get('ownershipUpdateType')?.disable();
      this.formGroup.get('dateReceived')?.disable();
      this.formGroup.get('isPendingDor')?.disable();
      this.formGroup.get('isReceivedAs608')?.disable();
      this.formGroup.get('isPendingDor').setValue(false);
    }
  }
}
